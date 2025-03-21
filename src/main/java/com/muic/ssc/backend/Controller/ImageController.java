package com.muic.ssc.backend.Controller;

import com.muic.ssc.backend.Entity.Image;
import com.muic.ssc.backend.Entity.User;
import com.muic.ssc.backend.Model.ImageGenPageModel.ImageGenRequest;
import com.muic.ssc.backend.Model.ImageGenPageModel.ImageGenResponse;
import com.muic.ssc.backend.Model.ImageGenPageModel.SaveImageRequest;
import com.muic.ssc.backend.Model.ImageGenPageModel.SaveImageResponse;
import com.muic.ssc.backend.Repository.UserRepository;
import com.muic.ssc.backend.Service.ImageGenService;
import com.muic.ssc.backend.Service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageGenService imageGenService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Generate an image based on the provided prompt and settings
     */
    @PostMapping("/generate")
    public ResponseEntity<ImageGenResponse> generateImage(@RequestBody ImageGenRequest request) {
        logger.info("Received image generation request with prompt: {}", request.getPrompt());

        try {
            // Validate the prompt
            if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                logger.warn("Empty prompt received in image generation request");

                return ResponseEntity.badRequest().body(
                        new ImageGenResponse("Error: No prompt provided. Image generation requires a valid prompt.", false)
                );
            }

            // Use the service to generate the image
            String result = imageGenService.generateImage(request.getPrompt(), request.getSettings());

            // Check if the result is an error message or URL
            if (result != null && result.startsWith("http")) {
                // Success - return the image URL
                logger.info("Successfully generated image with URL starting with: {}",
                        result.substring(0, Math.min(50, result.length())) + "...");

                return ResponseEntity.ok(new ImageGenResponse(result));
            } else {
                // Error from the service
                logger.warn("Error generating image: {}", result);
                return ResponseEntity.badRequest().body(
                        new ImageGenResponse(result, false)
                );
            }
        } catch (Exception e) {
            // Exception during processing
            logger.error("Exception during image generation", e);

            return ResponseEntity.internalServerError().body(
                    new ImageGenResponse("Internal server error: " + e.getMessage(), false)
            );
        }
    }

    /**
     * Save a generated image to the database, linked to the current user
     */
    @PostMapping("/save")
    public ResponseEntity<SaveImageResponse> saveImage(@RequestBody SaveImageRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Image savedImage = imageService.saveImageFromUrl(
                    request.getImageUrl(),
                    request.getInputPrompt(),
                    user.getId()
            );

            SaveImageResponse response = new SaveImageResponse(true, savedImage.getId(), "Image saved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SaveImageResponse response = new SaveImageResponse("Failed to save image: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Upload an image file and store it in S3, linked to the current user
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return ResponseEntity.status(403).body("You must be logged in to upload an image.");
            }

            String username = auth.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Image image = imageService.saveFileWithUser(file, user.getId());
            return ResponseEntity.ok(image);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Get images for the currently authenticated user
     */
    @GetMapping("/user")
    public ResponseEntity<List<Image>> getImagesForCurrentUser() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Image> images = imageService.getImagesByUsername(username);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all images (public gallery)
     */
    @GetMapping("/all")
    public ResponseEntity<List<Image>> getAllImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    /**
     * Get image by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        return imageService.getImageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete image by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        try {
            imageService.deleteImage(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
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
     * Checks if the user is authenticated and returns the authenticated User
     * @return User entity if authenticated, or throws an exception
     */
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new SecurityException("Authentication required");
        }

        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    /**
     * Generate an image based on the provided prompt and settings
     * This endpoint is public, so no authentication check
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
     * Requires authentication
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveImage(@RequestBody SaveImageRequest request) {
        try {
            // Get authenticated user
            User user = getAuthenticatedUser();

            Image savedImage = imageService.saveImageFromUrl(
                    request.getImageUrl(),
                    request.getInputPrompt(),
                    user.getId()
            );

            SaveImageResponse response = new SaveImageResponse(true, savedImage.getId(), "Image saved successfully");
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(
                    new SaveImageResponse(false, null, "Authentication required to save images")
            );
        } catch (Exception e) {
            SaveImageResponse response = new SaveImageResponse("Failed to save image: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Upload an image file and store it in S3, linked to the current user
     * Requires authentication
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Get authenticated user
            User user = getAuthenticatedUser();

            Image image = imageService.saveFileWithUser(file, user.getId());
            return ResponseEntity.ok(image);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("Authentication required to upload images");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Get images for the currently authenticated user
     * Requires authentication
     */
    @GetMapping("/user")
    public ResponseEntity<?> getImagesForCurrentUser() {
        try {
            // Get authenticated user
            User user = getAuthenticatedUser();

            List<Image> images = imageService.getImagesByUsername(user.getUsername());
            return ResponseEntity.ok(images);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("Authentication required to access user images");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving images: " + e.getMessage());
        }
    }

    /**
     * Get all images (public gallery)
     * This endpoint is public, so no authentication check
     */
    @GetMapping("/all")
    public ResponseEntity<List<Image>> getAllImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    /**
     * Update image visibility - requires authentication and ownership
     */
    @PatchMapping("/{id}/share")
    public ResponseEntity<?> updateImageVisibility(@PathVariable Long id, @RequestParam boolean isPublic) {
        try {
            // Get authenticated user
            User user = getAuthenticatedUser();

            Image updatedImage = imageService.updateImageVisibility(id, user.getUsername(), isPublic);
            return ResponseEntity.ok(updatedImage);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("Authentication required to update image visibility");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    /**
     * Get image by ID
     * This endpoint is public, but could be restricted based on image visibility
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getImageById(@PathVariable Long id) {
        try {
            return imageService.getImageById(id)
                    .map(image -> {
                        // Check if the image is public or owned by the current user
                        if (image.isPublic()) {
                            return ResponseEntity.ok(image);
                        }

                        // If not public, check if the current user is the owner
                        try {
                            User currentUser = getAuthenticatedUser();
                            if (image.getUser().getId().equals(currentUser.getId())) {
                                return ResponseEntity.ok(image);
                            }
                        } catch (SecurityException e) {
                            // User is not authenticated
                        }

                        // Image is private and user doesn't own it
                        return ResponseEntity.status(403).body("You don't have permission to view this private image");
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving image: " + e.getMessage());
        }
    }

    /**
     * Delete image by ID
     * Requires authentication and ownership
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        try {
            // Get authenticated user
            User user = getAuthenticatedUser();

            // Check if user owns the image
            Image image = imageService.getImageById(id)
                    .orElseThrow(() -> new RuntimeException("Image not found"));

            if (!image.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("You don't have permission to delete this image");
            }

            imageService.deleteImage(id);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("Authentication required to delete images");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting image: " + e.getMessage());
        }
    }
}
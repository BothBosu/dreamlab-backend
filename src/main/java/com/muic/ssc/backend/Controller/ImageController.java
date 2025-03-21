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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

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
        try {
            String imageUrl = imageGenService.generateImage(request.getPrompt(), request.getSettings());
            ImageGenResponse response = new ImageGenResponse(true, imageUrl, "Image generated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ImageGenResponse response = new ImageGenResponse("Failed to generate image: " + e.getMessage(), false);
            return ResponseEntity.badRequest().body(response);
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

            Image savedImage = imageService.saveImageUrlForUser(
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
    public ResponseEntity<Image> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Image image = imageService.saveFileWithUser(file, user.getId());
            return ResponseEntity.ok(image);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
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

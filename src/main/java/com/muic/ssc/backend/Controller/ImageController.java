package com.muic.ssc.backend.Controller;

import com.muic.ssc.backend.Entity.Image;
import com.muic.ssc.backend.Model.ImageGenPageModel.ImageGenRequest;
import com.muic.ssc.backend.Model.ImageGenPageModel.ImageGenResponse;
import com.muic.ssc.backend.Model.ImageGenPageModel.SaveImageRequest;
import com.muic.ssc.backend.Model.ImageGenPageModel.SaveImageResponse;
import com.muic.ssc.backend.Service.ImageGenService;
import com.muic.ssc.backend.Service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    /**
     * Generate an image based on the provided prompt and settings
     * When a POST request is made to /api/images/generate, the generateImage method is called with the request body.
     *
     * @return response with the generated image URL
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
     * Save a generated image to the database
     *
     * @param request request containing image URL and name
     * @return response with the saved image information
     */
    @PostMapping("/save")
    public ResponseEntity<SaveImageResponse> saveImage(@RequestBody SaveImageRequest request) {
        try {
            Image savedImage = imageGenService.saveGeneratedImage(request.getImageUrl(), request.getInputPrompt());

            SaveImageResponse response = new SaveImageResponse(true, savedImage.getId(), "Image saved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SaveImageResponse response = new SaveImageResponse("Failed to save image: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Image> uploadImage(@RequestParam("file") MultipartFile file) {
        Image image = imageService.save(file);
        return ResponseEntity.ok(image);
    }

    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> images = imageService.getAllImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        return imageService.getImageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}
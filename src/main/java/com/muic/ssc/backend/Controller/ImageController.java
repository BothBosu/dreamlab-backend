package com.muic.ssc.backend.Controller;

import com.muic.ssc.backend.Model.ImageGenPageModel.ImageGenRequest;
import com.muic.ssc.backend.Model.ImageGenPageModel.ImageGenResponse;
import com.muic.ssc.backend.Service.ImageGenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageGenService imageGenService;

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
}
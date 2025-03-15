package com.muic.ssc.backend.Service;

import com.muic.ssc.backend.Entity.Image;
import com.muic.ssc.backend.Utils.DebugUtils;

import com.muic.ssc.backend.Repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

/**
 * Author: Pawin Pothasuthon
 */
@Service
public class ImageGenService {

    private static final Logger logger = LoggerFactory.getLogger(ImageGenService.class);

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private RestTemplate restTemplate;

    // Placeholder for API URL - to be configured in application properties
    private String imageGenerationApiUrl;
    private String apiKey;

    /**
     * Generate an image based on the provided prompt and settings
     *
     * @param prompt the text prompt to generate image from
     * @param settings the generation settings (sampling method, steps, width, height, etc.)
     * @return the URL of the generated image
     */
    public String generateImage(String prompt, Map<String, Object> settings) {
        // Log detailed settings for debugging purposes
        DebugUtils.logImageGenerationSettings(prompt, settings);

        return "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Test.svg/2560px-Test.svg.png";
    }

    /**
     * Save the generated image to the database
     *
     * @return the saved image entity
     */
    public Image saveGeneratedImage(String imageUrl, String name) {
        logger.info("Saving generated image with name: {}", name);

        Image image = new Image();
        image.setName(name);
        image.setUrl(imageUrl);

        return imageRepository.save(image);
    }
}
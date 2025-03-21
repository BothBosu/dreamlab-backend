package com.muic.ssc.backend.Utils;

import com.muic.ssc.backend.Service.ImageGenService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageGenTest {
    private static Map<String, Object> createDefaultSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("response_format", "url");
        settings.put("seed", 125); // Fixed seed for reproducibility in tests

        // Default size settings
        settings.put("width", 512);
        settings.put("height", 512);
        settings.put("steps", 5);

        return settings;
    }

    private static void testGenerateImage(ImageGenService imageGenerator, String prompt, Map<String, Object> settings) {
        System.out.println("---------------------------------------------");
        System.out.println("Testing with prompt: " + (prompt != null ? "\"" + prompt + "\"" : "null"));
        System.out.println("Settings: " + settings);

        try {
            String result = imageGenerator.generateImage(prompt, settings);
            System.out.println("Result: " + result);

            // If the result is a URL, you could optionally validate it
            if (result != null && result.startsWith("http")) {
                System.out.println("Success: URL generated");
            } else {
                System.out.println("Note: Not a URL, possibly an error message");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("---------------------------------------------\n");
    }

    // Uncomment this to test the image generation service
    /*
    public static void main(String[] args) {
        ImageGenService imageGenerator = new ImageGenService();

        // Test with valid prompt and settings
        testGenerateImage(imageGenerator, "A beautiful sunset over the ocean", createDefaultSettings());

        // Test with custom settings
        Map<String, Object> customSettings = createDefaultSettings();
        customSettings.put("width", 1024);
        customSettings.put("height", 1024);
        customSettings.put("steps", 10);
        customSettings.put("negative_prompt", "blur, distortion, low quality");
        testGenerateImage(imageGenerator, "a futuristic cityscape at night with neon lights", customSettings);
    }
    */
}

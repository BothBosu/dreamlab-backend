package com.muic.ssc.backend.Service;

import com.muic.ssc.backend.Utils.DebugUtils;

import com.muic.ssc.backend.Repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
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

    // Placeholder for API settings
    private String apiKey = "key-NxVEIxOhxgQmT7Bkj07V8TgKhSvaUK1GhVTCCAwCeKHDKiPLZ9EPELd8Lq0qKVP54dPsniTPnIkvtvkkgToxLjfufBJKmXn";

    /**
     * Generate an image based on the provided prompt and settings
     *
     * @param prompt the text prompt to generate image from
     * @param settings the generation settings (sampling method, steps, width, height, etc.)
     * @return the URL of the generated image
     */
    public String generateImage(String prompt, Map<String, Object> settings) throws IOException, InterruptedException {
        // Log detailed settings for debugging purposes
        DebugUtils.logImageGenerationSettings(prompt, settings);

        // Check if prompt is null, empty, or just whitespace
        if (prompt == null || prompt.trim().isEmpty()) {
            return "Error: No prompt provided. Image generation requires a valid prompt.";
        }

        // Extract settings with defaults if not provided
        // negative_prompt will be empty if user didn't provide one
        String negativePrompt = settings.containsKey("negative_prompt") ? (String) settings.get("negative_prompt") : "";
        int width = settings.containsKey("width") ? ((Number) settings.get("width")).intValue() : 512;
        int height = settings.containsKey("height") ? ((Number) settings.get("height")).intValue() : 512;
        int steps = settings.containsKey("steps") ? ((Number) settings.get("steps")).intValue() : 5;
        String responseFormat = settings.containsKey("response_format") ? (String) settings.get("response_format") : "url";
        int seed = settings.containsKey("seed") ? ((Number) settings.get("seed")).intValue() : (int) (Math.random() * 1000000);

        // Build the request body as JSON
        String requestBody = String.format(
                "{\"prompt\":\"%s\",\"negative_prompt\":\"%s\",\"width\":%s,\"height\":%s,\"steps\":%s,\"response_format\":\"%s\",\"seed\":%s}",
                prompt, negativePrompt, Integer.toString(width), Integer.toString(height), Integer.toString(steps), responseFormat, Integer.toString(seed)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.getimg.ai/v1/stable-diffusion-xl/text-to-image"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("authorization", "Bearer " + apiKey)
                .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        // Parse the response to extract just the URL
        try {
            String responseBody = response.body();

            int urlStartIndex = responseBody.indexOf("\"url\":");

            if (urlStartIndex != -1) {
                urlStartIndex = responseBody.indexOf("\"", urlStartIndex + 6) + 1;
                int urlEndIndex = responseBody.indexOf("\"", urlStartIndex);
                if (urlStartIndex != -1 && urlEndIndex != -1) {
                    return responseBody.substring(urlStartIndex, urlEndIndex);
                }
            }

            // If URL extraction fails, return an error message
            return "Error: Could not extract URL from response: " + responseBody;
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }
}
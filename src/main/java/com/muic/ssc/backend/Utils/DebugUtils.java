package com.muic.ssc.backend.Utils;

import java.util.Map;

/**
 * Utility class for debugging and logging purposes.
 */
public class DebugUtils {

    /**
     * Logs the image generation settings provided by the user.
     * This method prints detailed information about the settings to the console,
     * making it easier to debug image generation issues.
     *
     * @param prompt The text prompt used for image generation
     * @param settings Map containing all the generation settings
     */
    public static void logImageGenerationSettings(String prompt, Map<String, Object> settings) {
        System.out.println("========== IMAGE GENERATION SETTINGS ==========");
        System.out.println("Prompt: " + prompt);
        System.out.println("Settings:");

        // Log each setting with proper formatting
        settings.forEach((key, value) -> {
            System.out.println("  " + key + " = " + value);
        });

        // Calculate and log additional useful information
        int width = settings.containsKey("width") ? ((Number) settings.get("width")).intValue() : 0;
        int height = settings.containsKey("height") ? ((Number) settings.get("height")).intValue() : 0;

        if (width > 0 && height > 0) {
            System.out.println("Image Dimensions: " + width + "x" + height +
                    " (Aspect Ratio: " + String.format("%.2f", (float) width / height) + ")");
        }

        System.out.println("==============================================");
    }

    /**
     * Logs any potential errors or warnings in the generation settings.
     * This can help identify configuration issues before they cause problems.
     *
     * @param settings The settings to validate
     */
    public static void validateAndLogSettings(Map<String, Object> settings) {
        System.out.println("Validating image generation settings...");

        // Check for missing required settings
        String[] requiredSettings = {"width", "height", "samplingMethod", "samplingSteps"};

        for (String required : requiredSettings) {
            if (!settings.containsKey(required)) {
                System.out.println("WARNING: Missing required setting: " + required);
            }
        }

        // Check for potentially problematic values
        if (settings.containsKey("width")) {
            int width = ((Number) settings.get("width")).intValue();

            if (width > 2048) {
                System.out.println("WARNING: Width value " + width + " is very high and may cause performance issues");
            }
        }

        if (settings.containsKey("height")) {
            int height = ((Number) settings.get("height")).intValue();

            if (height > 2048) {
                System.out.println("WARNING: Height value " + height + " is very high and may cause performance issues");
            }
        }

        if (settings.containsKey("samplingSteps")) {
            int steps = ((Number) settings.get("samplingSteps")).intValue();

            if (steps > 100) {
                System.out.println("WARNING: Sampling steps value " + steps + " is very high and may cause performance issues");
            }
        }

        System.out.println("Settings validation complete");
    }
}
package com.muic.ssc.backend.Model.ImageGenPageModel;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public class ImageGenRequest {
    // Added validation to ensure prompt is not blank
    @NotBlank(message = "Prompt cannot be empty")
    private String prompt;

    private Map<String, Object> settings;

    public ImageGenRequest() {
        // Initialize settings to prevent NPE
        this.settings = new HashMap<>();
    }

    public ImageGenRequest(String prompt, Map<String, Object> settings) {
        this.prompt = prompt;
        this.settings = settings != null ? settings : new HashMap<>();
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Map<String, Object> getSettings() {
        // Defensive null check
        if (settings == null) {
            settings = new HashMap<>();
        }
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "ImageGenRequest{" +
                "prompt='" + prompt + '\'' +
                ", settings=" + settings +
                '}';
    }
}
package com.muic.ssc.backend.model;

import java.util.Map;

public class ImageGenRequest {
    // Spring converts incoming JSON data into an instance.
    private String prompt;
    private Map<String, Object> settings;

    public ImageGenRequest() {
    }

    public ImageGenRequest(String prompt, Map<String, Object> settings) {
        this.prompt = prompt;
        this.settings = settings;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }
}
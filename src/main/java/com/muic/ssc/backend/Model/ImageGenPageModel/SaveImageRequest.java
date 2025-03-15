package com.muic.ssc.backend.model;

public class SaveImageRequest {
    private String imageUrl;
    private String name;

    public SaveImageRequest() {
    }

    public SaveImageRequest(String imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
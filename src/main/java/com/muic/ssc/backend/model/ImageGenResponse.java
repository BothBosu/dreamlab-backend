package com.muic.ssc.backend.model;

public class ImageGenResponse {
    private boolean success;
    private String imageUrl;
    private String message;

    public ImageGenResponse() {
    }

    public ImageGenResponse(boolean success, String imageUrl, String message) {
        this.success = success;
        this.imageUrl = imageUrl;
        this.message = message;
    }

    public ImageGenResponse(String imageUrl) {
        this.success = true;
        this.imageUrl = imageUrl;
        this.message = "Image generated successfully";
    }

    public ImageGenResponse(String message, boolean success) {
        this.success = false;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
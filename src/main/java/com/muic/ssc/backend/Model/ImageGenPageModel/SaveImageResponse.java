package com.muic.ssc.backend.Model.ImageGenPageModel;

public class SaveImageResponse {
    private boolean success;
    private Long imageId;
    private String message;

    public SaveImageResponse() {
    }

    public SaveImageResponse(boolean success, Long imageId, String message) {
        this.success = success;
        this.imageId = imageId;
        this.message = message;
    }

    public SaveImageResponse(Long imageId) {
        this.success = true;
        this.imageId = imageId;
        this.message = "Image saved successfully";
    }

    public SaveImageResponse(String message) {
        this.success = false;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
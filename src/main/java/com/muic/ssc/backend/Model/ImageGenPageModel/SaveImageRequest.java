package com.muic.ssc.backend.Model.ImageGenPageModel;

public class SaveImageRequest {
    private String imageUrl;
    private String inputPrompt;

    public SaveImageRequest() {
    }

    public SaveImageRequest(String imageUrl, String inputPrompt) {
        this.imageUrl = imageUrl;
        this.inputPrompt = inputPrompt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getInputPrompt() {
        return inputPrompt;
    }

    public void setInputPrompt(String inputPrompt) {
        this.inputPrompt = inputPrompt;
    }
}
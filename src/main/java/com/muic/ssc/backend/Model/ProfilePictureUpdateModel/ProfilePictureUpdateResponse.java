package com.muic.ssc.backend.Model.ProfilePictureUpdateModel;

public class ProfilePictureUpdateResponse {
    private String message;
    private boolean success;

    public ProfilePictureUpdateResponse() {
    }

    public ProfilePictureUpdateResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
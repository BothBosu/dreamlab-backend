package com.muic.ssc.backend.Model.LoginPageModel;

public class LoginResponse {
    private String message;
    private String username;
    private String profilePicture;
    private boolean success;

    public LoginResponse() {
    }

    public LoginResponse(String message, String username) {
        this.message = message;
        this.username = username;
        this.success = username != null;
    }

    public LoginResponse(String message, String username, String profilePicture, boolean success) {
        this.message = message;
        this.username = username;
        this.profilePicture = profilePicture;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
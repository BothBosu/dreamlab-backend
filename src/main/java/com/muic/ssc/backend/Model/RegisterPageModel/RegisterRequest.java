package com.muic.ssc.backend.Model.RegisterPageModel;

public class RegisterRequest {
    private String username;
    private String password;
    private String profilePicture;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String profilePicture) {
        this.username = username;
        this.password = password;
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
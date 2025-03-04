package com.muic.ssc.backend.model;

public class LoginResponse {
    private String message;
    private String username;

    public LoginResponse() {
    }

    public LoginResponse(String message, String username) {
        this.message = message;
        this.username = username;
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
}
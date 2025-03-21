package com.muic.ssc.backend.Model.ImageGenPageModel;

import org.springframework.http.HttpStatus;

public class ImageGenResponse {
    private boolean success;
    private String imageUrl;
    private String message;
    private HttpStatus status;

    public ImageGenResponse() {
        // Default to success with OK status
        this.success = true;
        this.status = HttpStatus.OK;
    }

    public ImageGenResponse(boolean success, String imageUrl, String message) {
        this.success = success;
        this.imageUrl = imageUrl;
        this.message = message;
        this.status = success ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
    }

    public ImageGenResponse(String imageUrl) {
        this.success = true;
        this.imageUrl = imageUrl;
        this.message = "Image generated successfully";
        this.status = HttpStatus.OK;
    }

    // Fixed constructor - the previous version set success to false but didn't use it properly
    public ImageGenResponse(String message, boolean success) {
        this.success = success;
        this.message = message;
        this.imageUrl = null;
        this.status = success ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
    }

    // Add a constructor specifically for auth errors
    public ImageGenResponse(String message, HttpStatus status) {
        this.success = false;
        this.message = message;
        this.status = status;
    }

    // Static factory method for authentication errors
    public static ImageGenResponse authError(String message) {
        return new ImageGenResponse(message != null ? message : "Authentication required", HttpStatus.FORBIDDEN);
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

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ImageGenResponse{" +
                "success=" + success +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
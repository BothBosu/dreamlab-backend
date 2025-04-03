package com.muic.ssc.backend.Model.ProfilePictureUpdateModel;

public class ProfilePictureUpdateRequest {
    private String profilePicture;

    public ProfilePictureUpdateRequest() {
    }

    public ProfilePictureUpdateRequest(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}

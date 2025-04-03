package com.muic.ssc.backend.Controller;

import com.muic.ssc.backend.Entity.User;
import com.muic.ssc.backend.Model.PasswordUpdateModel.PasswordUpdateRequest;
import com.muic.ssc.backend.Model.PasswordUpdateModel.PasswordUpdateResponse;
import com.muic.ssc.backend.Model.ProfilePictureUpdateModel.ProfilePictureUpdateRequest;
import com.muic.ssc.backend.Model.ProfilePictureUpdateModel.ProfilePictureUpdateResponse;
import com.muic.ssc.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current user profile information
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "User not found"
            ));
        }

        User user = userOptional.get();

        // Don't return sensitive information like password
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("profilePicture", user.getProfilePicture());
        response.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    /**
     * Update user password endpoint
     *
     * @param request PasswordUpdateRequest containing current and new password
     * @return PasswordUpdateResponse with success status and message
     */
    @PostMapping("/update-password")
    public ResponseEntity<PasswordUpdateResponse> updatePassword(@RequestBody PasswordUpdateRequest request) {
        // Validate that the new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.ok(new PasswordUpdateResponse("New passwords do not match", false));
        }

        // Check if new password meets minimum requirements
        if (request.getNewPassword().length() < 6) {
            return ResponseEntity.ok(new PasswordUpdateResponse("New password must be at least 6 characters long", false));
        }

        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        boolean updated = userService.updatePassword(
                username,
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        if (updated) {
            return ResponseEntity.ok(new PasswordUpdateResponse("Password updated successfully", true));
        } else {
            return ResponseEntity.ok(new PasswordUpdateResponse("Current password is incorrect", false));
        }
    }

    /**
     * Update user profile picture endpoint
     *
     * @param request ProfilePictureUpdateRequest containing the new profile picture
     * @return ProfilePictureUpdateResponse with success status and message
     */
    @PostMapping("/update-profile-picture")
    public ResponseEntity<ProfilePictureUpdateResponse> updateProfilePicture(@RequestBody ProfilePictureUpdateRequest request) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        boolean updated = userService.updateProfilePicture(
                username,
                request.getProfilePicture()
        );

        if (updated) {
            return ResponseEntity.ok(new ProfilePictureUpdateResponse("Profile picture updated successfully", true));
        } else {
            return ResponseEntity.ok(new ProfilePictureUpdateResponse("Failed to update profile picture", false));
        }
    }

    /**
     * Get available profile pictures
     */
    @GetMapping("/profile-pictures")
    public ResponseEntity<?> getAvailableProfilePictures() {
        // Here we're returning a hardcoded list of available profile pictures
        // In a real app, you might scan a directory or fetch from a database
        String[] profilePictures = {
                "avatar1.png",
                "avatar2.png",
                "avatar3.png",
                "avatar4.png",
                "avatar5.png",
                "avatar6.png",
                "avatar7.png",
                "avatar8.png"
        };

        return ResponseEntity.ok(Map.of(
                "success", true,
                "profilePictures", profilePictures
        ));
    }
}
package com.muic.ssc.backend.Controller;

import com.muic.ssc.backend.Model.PasswordUpdateModel.PasswordUpdateRequest;
import com.muic.ssc.backend.Model.PasswordUpdateModel.PasswordUpdateResponse;
import com.muic.ssc.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Update user password endpoint
     *
     * @param request PasswordUpdateRequest containing current, new, and confirmation password
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
}

package com.muic.ssc.backend.Controller;

import com.muic.ssc.backend.Service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * Toggle like status for an image by the currently authenticated user
     */
    @PostMapping("/{imageId}/toggle")
    public ResponseEntity<?> toggleLike(@PathVariable Long imageId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return ResponseEntity.status(403).body("You must be logged in to like an image.");
            }

            String username = auth.getName();
            likeService.toggleLike(imageId, username);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error toggling like: " + e.getMessage());
        }
    }

    /**
     * Get like count for an image (public)
     */
    @GetMapping("/{imageId}/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long imageId) {
        try {
            Long count = likeService.getLikeCount(imageId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

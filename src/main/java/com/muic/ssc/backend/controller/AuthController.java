package com.muic.ssc.backend.controller;

import com.muic.ssc.backend.model.LoginRequest;
import com.muic.ssc.backend.model.LoginResponse;
import com.muic.ssc.backend.model.RegisterRequest;
import com.muic.ssc.backend.model.RegisterResponse;
import com.muic.ssc.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            userService.registerNewUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword()
            );

            return ResponseEntity.ok(new RegisterResponse("User registered successfully", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new RegisterResponse(
                    "Registration failed: " + e.getMessage(), false
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());

            if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid credentials");
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok(new LoginResponse("Login successful", userDetails.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LoginResponse("Login failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.ok(new LoginResponse("Authenticated", authentication.getName()));
        }

        return ResponseEntity.ok(new LoginResponse("Not authenticated", null));
    }
}
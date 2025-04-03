package com.muic.ssc.backend.Controller;

import com.muic.ssc.backend.Model.LoginPageModel.LoginRequest;
import com.muic.ssc.backend.Model.LoginPageModel.LoginResponse;
import com.muic.ssc.backend.Model.RegisterPageModel.RegisterRequest;
import com.muic.ssc.backend.Model.RegisterPageModel.RegisterResponse;
import com.muic.ssc.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

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
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Set authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Store authentication in session
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            return ResponseEntity.ok(new LoginResponse("Login successful", authentication.getName()));
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // Destroy the session
        return ResponseEntity.ok("Logged out successfully");
    }
}

package com.example.blog.controller;

import com.example.blog.dto.AuthResponse;
import com.example.blog.dto.RegisterRequest;
import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * One-time admin setup controller.
 * This endpoint allows creating the single admin user using a secret setup key.
 * After the admin is created, this endpoint becomes useless.
 */
@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
@Slf4j
public class AdminSetupController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${admin.setup.key:}")
    private String adminSetupKey;

    /**
     * POST /api/setup/admin - Create the admin user (one-time setup)
     *
     * Requires the ADMIN_SETUP_KEY header to match the environment variable.
     * Will fail if any user already exists in the database.
     */
    @PostMapping("/admin")
    public ResponseEntity<?> setupAdmin(
            @RequestHeader(value = "X-Setup-Key", required = false) String setupKey,
            @Valid @RequestBody RegisterRequest request
    ) {
        // Check if setup key is configured
        if (adminSetupKey == null || adminSetupKey.isBlank()) {
            log.warn("Admin setup attempted but ADMIN_SETUP_KEY is not configured");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Admin setup is not enabled. Set ADMIN_SETUP_KEY environment variable."));
        }

        // Validate the setup key
        if (setupKey == null || !setupKey.equals(adminSetupKey)) {
            log.warn("Admin setup attempted with invalid setup key");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Invalid setup key"));
        }

        // Check if any user already exists (single admin policy)
        if (userRepository.count() > 0) {
            log.warn("Admin setup attempted but users already exist in database");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Admin user already exists. Setup can only be run once."));
        }

        // Create the admin user
        User admin = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername())
                .role(User.Role.ADMIN)
                .enabled(true)
                .build();

        User savedAdmin = userRepository.save(admin);
        log.info("Admin user created successfully: username={}, email={}", savedAdmin.getUsername(), savedAdmin.getEmail());

        String token = jwtService.generateToken(savedAdmin.getUsername(), savedAdmin.getRole().name());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                AuthResponse.builder()
                        .accessToken(token)
                        .tokenType("Bearer")
                        .expiresIn(jwtService.getExpirationTime() / 1000)
                        .user(AuthResponse.UserResponse.builder()
                                .id(savedAdmin.getId())
                                .username(savedAdmin.getUsername())
                                .email(savedAdmin.getEmail())
                                .displayName(savedAdmin.getDisplayName())
                                .role(savedAdmin.getRole().name())
                                .build())
                        .build()
        );
    }

    /**
     * GET /api/setup/status - Check if setup is needed
     */
    @GetMapping("/status")
    public ResponseEntity<?> getSetupStatus() {
        boolean setupComplete = userRepository.count() > 0;
        boolean setupEnabled = adminSetupKey != null && !adminSetupKey.isBlank();

        return ResponseEntity.ok(new SetupStatus(setupComplete, setupEnabled));
    }

    record ErrorResponse(String message) {}
    record SetupStatus(boolean setupComplete, boolean setupEnabled) {}
}

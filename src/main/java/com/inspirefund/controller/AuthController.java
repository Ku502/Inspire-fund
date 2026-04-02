package com.inspirefund.controller;

import com.inspirefund.dto.*;
import com.inspirefund.entity.User;
import com.inspirefund.repository.UserRepository;
import com.inspirefund.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Email already registered."));
        }
        User user = User.builder()
            .name(req.getName())
            .email(req.getEmail())
            .passwordHash(passwordEncoder.encode(req.getPassword()))
            .phone(req.getPhone())
            .role(User.Role.DONOR)
            .build();
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Account created successfully."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return userRepository.findByEmail(req.getEmail())
            .filter(u -> passwordEncoder.matches(req.getPassword(), u.getPasswordHash()))
            .map(u -> {
                String token = jwtUtils.generateToken(u.getEmail(), u.getRole().name());
                return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", u.getRole().name(),
                    "name", u.getName(),
                    "email", u.getEmail()
                ));
            })
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid email or password.")));
    }
}

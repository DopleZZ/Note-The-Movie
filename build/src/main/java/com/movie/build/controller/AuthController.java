package com.movie.build.controller;

import com.movie.build.dto.AuthRequest;
import com.movie.build.dto.AuthResponse;
import com.movie.build.model.User;
import com.movie.build.service.UserService;
import com.movie.build.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest req) {
        try {
            User u = userService.register(req.getUsername(), req.getPassword());
            String token = jwtUtil.generateToken(u.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, u.getUsername()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
        try {
            User u = userService.authenticate(req.getUsername(), req.getPassword());
            String token = jwtUtil.generateToken(u.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, u.getUsername()));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}

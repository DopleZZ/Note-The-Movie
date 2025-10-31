package com.note.backend.controller;

import com.note.backend.dto.AuthRequest;
import com.note.backend.dto.AuthResponse;
import com.note.backend.model.User;
import com.note.backend.service.JwtService;
import com.note.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest req) {
        if (req.getUsername().length() < 3 || req.getPassword().length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверные данные");
        }
        try {
            User user = userService.register(req.getUsername(), req.getPassword());
            String token = jwtService.generateToken(userService.loadUserByUsername(user.getUsername()));
            return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
        try {
            User user = userService.findByUsernameOrThrow(req.getUsername());
            if (!userService.checkPassword(user, req.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
            }
            String token = jwtService.generateToken(userService.loadUserByUsername(user.getUsername()));
            return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
        }
    }
}


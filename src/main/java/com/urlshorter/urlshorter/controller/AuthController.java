package com.urlshorter.urlshorter.controller;

import com.urlshorter.urlshorter.controller.payload.request.LoginRequestDTO;
import com.urlshorter.urlshorter.controller.payload.request.RegisterRequestDTO;
import com.urlshorter.urlshorter.controller.payload.response.TokenResponseDTO;
import com.urlshorter.urlshorter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO req) {
        try {
            TokenResponseDTO response = authService.register(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO req) {
        try {
            TokenResponseDTO token = authService.login(req);
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            log.warn("[WARN] Login fallito: {}", req.username());
            return ResponseEntity.status(401).body("Credenziali non valide");
        }
    }
}
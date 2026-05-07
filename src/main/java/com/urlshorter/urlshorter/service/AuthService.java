package com.urlshorter.urlshorter.service;

import com.urlshorter.urlshorter.controller.payload.request.LoginRequestDTO;
import com.urlshorter.urlshorter.controller.payload.request.RegisterRequestDTO;
import com.urlshorter.urlshorter.controller.payload.response.TokenResponseDTO;
import com.urlshorter.urlshorter.security.CustomUserDetails;
import com.urlshorter.urlshorter.security.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final UserService userService;
    private final JWTService jwtService;
    private final AuthenticationManager authManager;

    public AuthService(UserService userService, JWTService jwtService, AuthenticationManager authManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    public TokenResponseDTO register(RegisterRequestDTO req) {
        // Dopo la registrazione faccio subito il login e restituisco il token.
        userService.register(req);
        return login(new LoginRequestDTO(req.username(), req.password()));
    }

    public TokenResponseDTO login(LoginRequestDTO req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(ud.getUsername(), ud.getRoles());

        log.info("Login effettuato per {}", ud.getUsername());
        return new TokenResponseDTO(token, "Bearer");
    }
}

package com.urlshorter.urlshorter.service;

import com.urlshorter.urlshorter.controller.payload.request.RegisterRequestDTO;
import com.urlshorter.urlshorter.entity.UserEntity;
import com.urlshorter.urlshorter.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public void register(RegisterRequestDTO req) {
        if (userRepo.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username gia in uso");
        }

        String hashed = encoder.encode(req.password());
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        UserEntity newUser = new UserEntity(req.username(), hashed, roles);
        userRepo.save(newUser);

        log.info("Utente {} salvato a DB", newUser.getUsername());
    }
}

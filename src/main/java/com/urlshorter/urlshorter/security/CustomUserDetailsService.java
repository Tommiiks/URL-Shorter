package com.urlshorter.urlshorter.security;

import com.urlshorter.urlshorter.entity.UserEntity;
import com.urlshorter.urlshorter.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userOptional = repo.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Credenziali non valide!");
        }

        return new CustomUserDetails(userOptional.get());
    }
}

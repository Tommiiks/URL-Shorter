package com.urlshorter.urlshorter.controller;

import com.urlshorter.urlshorter.controller.payload.request.ShortUrlRequestDTO;
import com.urlshorter.urlshorter.controller.payload.response.ShortUrlResponseDTO;
import com.urlshorter.urlshorter.service.ShortUrlService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/urls")
public class ShortUrlController {

    private final ShortUrlService shortUrlServ;

    public ShortUrlController(ShortUrlService shortUrlServ) {
        this.shortUrlServ = shortUrlServ;
    }

    /* --- CRUD --- */

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ShortUrlResponseDTO> findAll(Authentication authentication, Pageable pageable) {
        String username = authentication.getName();
        return shortUrlServ.findAllByOwnerUsername(username, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShortUrlResponseDTO create(@Valid @RequestBody ShortUrlRequestDTO dto, Authentication authentication) {
        String username = authentication.getName();
        return shortUrlServ.create(dto, username);
    }

    @GetMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.OK)
    public ShortUrlResponseDTO findByShortCode(@PathVariable String shortCode, Authentication authentication) {
        String username = authentication.getName();
        return shortUrlServ.findByShortCode(shortCode, username);
    }

    @PutMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.OK)
    public ShortUrlResponseDTO update(@PathVariable String shortCode,
                                      @Valid @RequestBody ShortUrlRequestDTO dto,
                                      Authentication authentication) {
        String username = authentication.getName();
        return shortUrlServ.update(shortCode, dto, username);
    }

    @DeleteMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String shortCode, Authentication authentication) {
        String username = authentication.getName();
        List<String> roles = new ArrayList<>();

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            roles.add(authority.getAuthority());
        }

        shortUrlServ.delete(shortCode, username, roles);
    }

    /* --- Stats --- */

    @GetMapping("/{shortCode}/stats")
    @ResponseStatus(HttpStatus.OK)
    public ShortUrlResponseDTO getStats(@PathVariable String shortCode, Authentication authentication) {
        String username = authentication.getName();
        return shortUrlServ.stats(shortCode, username);
    }
}

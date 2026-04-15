package com.urlshorter.urlshorter.controller;

import com.urlshorter.urlshorter.service.RedirectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("api/v1/redirect")
@RestController
public class RedirectController {
    private final RedirectService redirectServ;

    public RedirectController(RedirectService redirectServ) {
        this.redirectServ = redirectServ;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode){
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectServ.redirect(shortCode)))
                .build();
    }

}

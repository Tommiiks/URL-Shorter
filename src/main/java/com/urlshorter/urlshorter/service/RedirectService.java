package com.urlshorter.urlshorter.service;

import com.urlshorter.urlshorter.entity.ShortUrlEntity;
import com.urlshorter.urlshorter.exception.UrlExpiredException;
import com.urlshorter.urlshorter.repository.ShortUrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class RedirectService {

    private final ShortUrlService shortUrlServ;
    private final ShortUrlRepository shortUrlRepo;

    public RedirectService(ShortUrlService shortUrlServ, ShortUrlRepository shortUrlRepo) {
        this.shortUrlServ = shortUrlServ;
        this.shortUrlRepo = shortUrlRepo;
    }

    @Transactional
    public String redirect(String shortCode) {
        ShortUrlEntity url = shortUrlRepo.findByShortCode(shortCode)
                .orElseThrow(() -> new EntityNotFoundException("The Short Code dont exist: " + shortCode));

        LocalDateTime expiresAt = url.getExpiresAt();

        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            throw new UrlExpiredException(shortCode);
        }

        shortUrlServ.incrementAccessCount(shortCode);
        log.info("Redirect eseguito per {}", shortCode);

        return url.getOriginalUrl();
    }
}

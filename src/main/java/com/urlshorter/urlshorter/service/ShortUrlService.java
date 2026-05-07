package com.urlshorter.urlshorter.service;

import com.urlshorter.urlshorter.controller.payload.request.ShortUrlRequestDTO;
import com.urlshorter.urlshorter.controller.payload.response.ShortUrlResponseDTO;
import com.urlshorter.urlshorter.entity.ShortUrlEntity;
import com.urlshorter.urlshorter.entity.UserEntity;
import com.urlshorter.urlshorter.repository.ShortUrlRepository;
import com.urlshorter.urlshorter.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepo;
    private final UserRepository userRepo;

    public ShortUrlService(ShortUrlRepository shortUrlRepo, UserRepository userRepo) {
        this.shortUrlRepo = shortUrlRepo;
        this.userRepo = userRepo;
    }

    /* --- Read --- */

    public Page<ShortUrlResponseDTO> findAllByOwnerUsername(String username, Pageable pageable) {
        Page<ShortUrlEntity> entityPage = shortUrlRepo.findByOwnerUsername(username, pageable);
        List<ShortUrlResponseDTO> responseList = new ArrayList<>();

        for (ShortUrlEntity entity : entityPage.getContent()) {
            responseList.add(toResponseDTO(entity));
        }

        return new PageImpl<>(responseList, pageable, entityPage.getTotalElements());
    }

    /* --- Create --- */

    @Transactional
    public ShortUrlResponseDTO create(ShortUrlRequestDTO dto, String username) {
        ShortUrlEntity shortUrlEntity = toEntity(dto);
        UserEntity userEntity = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("The Owner dont exist: " + username));

        shortUrlEntity.setOwner(userEntity);
        shortUrlEntity.setShortCode(resolveShortCode(dto));
        shortUrlEntity.setCreatedAt(LocalDateTime.now());
        shortUrlEntity.setUpdatedAt(LocalDateTime.now());

        ShortUrlEntity saved = shortUrlRepo.save(shortUrlEntity);
        log.info("Short URL creato: {} da {}", saved.getShortCode(), username);

        return toResponseDTO(saved);
    }

    public ShortUrlResponseDTO findByShortCode(String shortCode, String username) {
        ShortUrlEntity entity = findShortUrlOrThrow(shortCode);

        if (!entity.getOwner().getUsername().equals(username)) {
            log.warn("Tentativo di accesso non autorizzato al dettaglio di {} da {}", shortCode, username);
            throw new EntityNotFoundException("The Short Code dont exist: " + shortCode);
        }

        return toResponseDTO(entity);
    }

    /* --- Update --- */

    @Transactional
    public ShortUrlResponseDTO update(String shortCode, ShortUrlRequestDTO dto, String username) {
        ShortUrlEntity shortUrlEntity = findShortUrlOrThrow(shortCode);

        if (!shortUrlEntity.getOwner().getUsername().equals(username)) {
            log.warn("Tentativo di modifica non autorizzato su {} da {}", shortCode, username);
            throw new AccessDeniedException("You can't modify the short code because you are not the owner.");
        }

        shortUrlEntity.setOriginalUrl(dto.getOriginalUrl());
        shortUrlEntity.setExpiresAt(dto.getExpiresAt());
        shortUrlEntity.setUpdatedAt(LocalDateTime.now());

        ShortUrlEntity saved = shortUrlRepo.save(shortUrlEntity);
        return toResponseDTO(saved);
    }

    /* --- Delete --- */

    @Transactional
    public void delete(String shortCode, String username, List<String> roles) {
        ShortUrlEntity shortUrlEntity = findShortUrlOrThrow(shortCode);

        if (!shortUrlEntity.getOwner().getUsername().equals(username) && !roles.contains("ROLE_ADMIN")) {
            log.warn("Tentativo di cancellazione non autorizzato su {} da {}", shortCode, username);
            throw new AccessDeniedException(
                    "You can't delete the short code because you are not the owner or didnt have permess to do that."
            );
        }

        shortUrlRepo.delete(shortUrlEntity);
    }

    /* --- Stats --- */

    public ShortUrlResponseDTO stats(String shortCode, String username) {
        ShortUrlEntity url = findShortUrlOrThrow(shortCode);

        if (!url.getOwner().getUsername().equals(username)) {
            log.warn("Tentativo di accesso alle stats non autorizzato su {} da {}", shortCode, username);
            throw new AccessDeniedException("You can't see the stats of that short code.");
        }

        return toResponseDTO(url);
    }

    public void incrementAccessCount(String shortCode) {
        shortUrlRepo.incrementAccessCount(shortCode);
    }

    /* --- Helpers --- */

    private String resolveShortCode(ShortUrlRequestDTO dto) {
        if (dto.getCustomAlias() != null) {
            if (shortUrlRepo.existsByShortCode(dto.getCustomAlias())) {
                log.warn("Tentativo di creare alias gia esistente: {}", dto.getCustomAlias());
                throw new IllegalArgumentException("Custom alias gia in uso: " + dto.getCustomAlias());
            }

            return dto.getCustomAlias();
        }

        String shortCode;

        do {
            shortCode = generateShortCode();
        } while (shortUrlRepo.existsByShortCode(shortCode));

        return shortCode;
    }

    private String generateShortCode() {
        String chars = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder shortCode = new StringBuilder();
        Random random = new Random();

        while (shortCode.length() < 6) {
            int index = random.nextInt(chars.length());
            shortCode.append(chars.charAt(index));
        }

        return shortCode.toString();
    }

    private ShortUrlEntity findShortUrlOrThrow(String shortCode) {
        return shortUrlRepo.findByShortCode(shortCode)
                .orElseThrow(() -> new EntityNotFoundException("The Short Code dont exist: " + shortCode));
    }

    /* --- Mapping --- */

    private ShortUrlEntity toEntity(ShortUrlRequestDTO dto) {
        ShortUrlEntity entity = new ShortUrlEntity();
        entity.setOriginalUrl(dto.getOriginalUrl());
        entity.setExpiresAt(dto.getExpiresAt());
        return entity;
    }

    private ShortUrlResponseDTO toResponseDTO(ShortUrlEntity entity) {
        ShortUrlResponseDTO dto = new ShortUrlResponseDTO();
        dto.setShortCode(entity.getShortCode());
        dto.setOriginalUrl(entity.getOriginalUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setExpiresAt(entity.getExpiresAt());
        dto.setAccessCount(entity.getAccessCount());
        return dto;
    }
}

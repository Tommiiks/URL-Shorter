package com.urlshorter.urlshorter.repository;

import com.urlshorter.urlshorter.entity.ShortUrlEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntity, Long> {

    Optional<ShortUrlEntity> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    Page<ShortUrlEntity> findByOwnerUsername(String username, Pageable pageable);

    // Aggiorna solo il contatore accessi.
    @Modifying
    @Transactional
    @Query("UPDATE ShortUrlEntity u SET u.accessCount = u.accessCount + 1 WHERE u.shortCode = :shortCode")
    void incrementAccessCount(String shortCode);
}

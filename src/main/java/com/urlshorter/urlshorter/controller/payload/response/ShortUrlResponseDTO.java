package com.urlshorter.urlshorter.controller.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortUrlResponseDTO {

    private String shortCode;
    private String originalUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    private Long accessCount;


}

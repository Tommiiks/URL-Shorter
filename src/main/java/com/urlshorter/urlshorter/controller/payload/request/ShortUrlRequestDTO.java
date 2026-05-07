package com.urlshorter.urlshorter.controller.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortUrlRequestDTO {

    @NotBlank
    @org.hibernate.validator.constraints.URL
    private String originalUrl;

    @Size(min=3,max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String customAlias;

    private LocalDateTime expiresAt;


}

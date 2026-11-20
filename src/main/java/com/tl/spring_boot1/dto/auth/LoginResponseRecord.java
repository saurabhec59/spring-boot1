package com.tl.spring_boot1.dto.auth;

public record LoginResponseRecord(
        LoginResponseDTO loginResponseDTO,
        String refreshToken,
        Integer expiresIn
) { }

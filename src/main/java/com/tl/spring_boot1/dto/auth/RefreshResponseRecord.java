package com.tl.spring_boot1.dto.auth;

public record RefreshResponseRecord(
        RefreshResponseDTO refreshResponseDTO,
        String newRefreshToken,
        Integer expiresIn
) { }

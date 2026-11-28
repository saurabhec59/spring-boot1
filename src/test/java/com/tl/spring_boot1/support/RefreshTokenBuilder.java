package com.tl.spring_boot1.support;

import com.tl.spring_boot1.model.RefreshToken;
import com.tl.spring_boot1.model.RefreshTokenStatus;

import java.time.Instant;

public class RefreshTokenBuilder {
    private Integer id = 1;
    private String tokenHash = "refreshTokenHash";
    private Integer userId = 1;
    private Instant createdAt = Instant.now();
    private Instant expiresAt = Instant.now().plusSeconds(604800);
    private RefreshTokenStatus status = RefreshTokenStatus.ACTIVE;

    public static RefreshTokenBuilder aRefreshToken(){
        return new RefreshTokenBuilder();
    }

    public RefreshTokenBuilder withId(Integer id){
        this.id = id;
        return this;
    }

    public RefreshTokenBuilder withTokenHash(String tokenHash){
        this.tokenHash = tokenHash;
        return this;
    }

    public RefreshTokenBuilder withUserId(Integer userId){
        this.userId = userId;
        return this;
    }

    public RefreshTokenBuilder withCreatedAt(Instant createdAt){
        this.createdAt = createdAt;
        return this;
    }

    public RefreshTokenBuilder withExpiresAt(Instant expiresAt){
        this.expiresAt = expiresAt;
        return this;
    }

    public RefreshTokenBuilder withStatus(RefreshTokenStatus status){
        this.status = status;
        return this;
    }

    public RefreshToken build(){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(id);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setUserId(userId);
        refreshToken.setCreatedAt(createdAt);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setStatus(status);
        return refreshToken;
    }
}

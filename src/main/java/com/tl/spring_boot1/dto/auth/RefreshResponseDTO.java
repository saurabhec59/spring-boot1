package com.tl.spring_boot1.dto.auth;

public class RefreshResponseDTO {
    private final String token;
    private final String tokenType;
    private final Integer expiresIn;

    private RefreshResponseDTO(String token, String tokenType, Integer expiresIn) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public static RefreshResponseDTO from(String token, String tokenType, Integer expiresIn) {
        return new RefreshResponseDTO(token, tokenType, expiresIn);
    }

    public String getToken(){ return token;}
    public String getTokenType(){ return tokenType;}
    public Integer getExpiresIn(){ return expiresIn;}

}

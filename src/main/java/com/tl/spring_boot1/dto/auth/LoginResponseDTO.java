package com.tl.spring_boot1.dto.auth;

import com.tl.spring_boot1.dto.users.UserResponse;

public class LoginResponseDTO {

    private final String token;
    private final String tokenType;
    private final Integer expiresIn;
    private final UserResponse user;

    private LoginResponseDTO(String token,  String tokenType, Integer expiresIn, UserResponse user) {
        this.token = token;
        this.tokenType = tokenType;
        this.user = user;
        this.expiresIn = expiresIn;
    }

    public static LoginResponseDTO from(String token,  String tokenType, Integer expiresIn, UserResponse user) {
        return new LoginResponseDTO(token, tokenType, expiresIn, user);
    }

    public String getToken() {
        return token;
    }
    public String getTokenType() {
        return tokenType;
    }
    public Integer getExpiresIn() {
        return expiresIn;
    }
    public UserResponse getUser() {
        return user;
    }
}

package com.tl.spring_boot1.controller;

import com.tl.spring_boot1.dto.auth.*;
import com.tl.spring_boot1.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login( @Valid @RequestBody LoginRequestDTO request){
        LoginResponseRecord responseRecord =  authService.login(request);

        // build 'Set-Cookie' header
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", responseRecord.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(responseRecord.expiresIn())
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(responseRecord.loginResponseDTO());
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<RefreshResponseDTO> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken){
        if(refreshToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RefreshResponseRecord responseRecord = authService.refresh(refreshToken);

        // build 'Set-Cookie' header
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", responseRecord.newRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(responseRecord.expiresIn())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(responseRecord.refreshResponseDTO());
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken){
        // even if client did not send refresh_token cookie then also we will send no error because logout is idempotent. That's why we
        // do required = false, Also we will send no cookie and maxAge=0 to client so that client can delete the cookie from its side.
        authService.logout(refreshToken);

        // build 'Cookie' header so that client can delete the refresh_token from cookie
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(0)
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT) // 204 No Content
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }
}

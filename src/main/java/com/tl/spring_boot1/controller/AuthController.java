package com.tl.spring_boot1.controller;

import com.tl.spring_boot1.dto.auth.LoginRequestDTO;
import com.tl.spring_boot1.dto.auth.LoginResponseDTO;
import com.tl.spring_boot1.dto.auth.LoginResponseRecord;
import com.tl.spring_boot1.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
}

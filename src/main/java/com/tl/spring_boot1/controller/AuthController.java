package com.tl.spring_boot1.controller;

import com.tl.spring_boot1.dto.auth.LoginRequestDTO;
import com.tl.spring_boot1.dto.auth.LoginResponseDTO;
import com.tl.spring_boot1.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        LoginResponseDTO response =  authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

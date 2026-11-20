package com.tl.spring_boot1.service;

import com.tl.spring_boot1.dto.auth.LoginRequestDTO;
import com.tl.spring_boot1.dto.auth.LoginResponseDTO;
import com.tl.spring_boot1.dto.auth.LoginResponseRecord;
import com.tl.spring_boot1.dto.users.UserResponse;
import com.tl.spring_boot1.exception.InvalidUserDataException;
import com.tl.spring_boot1.model.RefreshToken;
import com.tl.spring_boot1.model.User;
import com.tl.spring_boot1.repository.RefreshTokenRepository;
import com.tl.spring_boot1.repository.UserRepository;
import com.tl.spring_boot1.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.tl.spring_boot1.util.RefreshTokenUtil;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenUtil  refreshTokenUtil;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration-seconds}")
    private Integer refreshTokenExpirationSeconds;

    @Transactional
    public LoginResponseRecord login(LoginRequestDTO request){

        // check if user with email exists or not
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if(user == null){
            throw new InvalidUserDataException("Invalid email or password");// not authenticated client should not know whether this username is present or not.
        }
        //
        if( !passwordEncoder.matches( request.getPassword(), user.getPassword()) ){
            throw new InvalidUserDataException("Invalid email or password");
        }

        // generate jwt
        final String accessToken = jwtUtil.generateJwtToken(user);

        // generate refreshToken
        String refreshToken = refreshTokenUtil.generateRefreshToken();
        String refreshTokenHash = refreshTokenUtil.generateRefreshTokenHash(refreshToken);

        // save hashed token and return original token
        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setTokenHash(refreshTokenHash); // storing hashed token only
        refreshTokenObj.setUserId(user.getId());
        Instant now = Instant.now();
        refreshTokenObj.setCreatedAt(now);
        refreshTokenObj.setExpiresAt(now.plusSeconds(refreshTokenExpirationSeconds));

        // delete the existing refresh tokens from db before saving new one
        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(refreshTokenObj); // no need to store

        // user details (UserResponse dto, not sending 'user' object directly)
        // In response body still the LoginResponseDTO is sent but additionally refresh_token, and it's expiry needs to be sent as header so
        // body and these header details are passed in java 'record' and record is returned from service, rest header and body formed by controller.
        return new LoginResponseRecord(LoginResponseDTO.from(accessToken, "Bearer", jwtUtil.getExpirationTimeInSeconds(), UserResponse.from(user) ), refreshToken, refreshTokenExpirationSeconds);
    }
}

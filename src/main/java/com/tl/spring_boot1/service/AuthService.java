package com.tl.spring_boot1.service;

import com.tl.spring_boot1.dto.auth.*;
import com.tl.spring_boot1.dto.users.UserResponse;
import com.tl.spring_boot1.exception.AuthenticationException;
import com.tl.spring_boot1.exception.InvalidUserDataException;
import com.tl.spring_boot1.model.RefreshToken;
import com.tl.spring_boot1.model.RefreshTokenStatus;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenUtil  refreshTokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final Integer refreshTokenExpirationSeconds;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            RefreshTokenUtil  refreshTokenUtil,
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenService refreshTokenService,
            @Value("${jwt.refresh-token-expiration-seconds}") Integer refreshTokenExpirationSeconds
    ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenUtil = refreshTokenUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
    }

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
        refreshTokenObj.setStatus(RefreshTokenStatus.ACTIVE);

        refreshTokenRepository.save(refreshTokenObj); // no need to store

        // user details (UserResponse dto, not sending 'user' object directly)
        // In response body still the LoginResponseDTO is sent but additionally refresh_token, and it's expiry needs to be sent as header so
        // body and these header details are passed in java 'record' and record is returned from service, rest header and body formed by controller.
        return new LoginResponseRecord(LoginResponseDTO.from(accessToken, "Bearer", jwtUtil.getExpirationTimeInSeconds(), UserResponse.from(user) ), refreshToken, refreshTokenExpirationSeconds);
    }

    @Transactional
    public RefreshResponseRecord refresh(String refreshToken){

        // hash the refresh token
        String refreshTokenHash = refreshTokenUtil.generateRefreshTokenHash(refreshToken);
        // see if this tokenHash is present in db or not
        RefreshToken stored = refreshTokenRepository.findByTokenHash(refreshTokenHash).orElse(null);
        // if token not present send 401
        if(stored == null){
            throw new AuthenticationException("Invalid refresh token");
        }
        // if status is not active then revoke all
        if(stored.getStatus() != RefreshTokenStatus.ACTIVE){
            // set status to 'REVOKED' of to all refresh tokens issued to this user
            refreshTokenService.revokeAllForUser(stored.getUserId());
            throw new AuthenticationException("Invalid refresh token");
        }
        // token is active, if it is expired then  return 401
        if(stored.getExpiresAt().isBefore(Instant.now())){
            throw new AuthenticationException("Refresh token expired");
        }
        // token is active and valid, mark this one as 'ROTATED', create and send new one and store its hash, also create and send jwt access token
        // create refresh token and save in db
        stored.setStatus(RefreshTokenStatus.ROTATED);

        String newRefreshToken = refreshTokenUtil.generateRefreshToken();
        String newRefreshTokenHash = refreshTokenUtil.generateRefreshTokenHash(newRefreshToken);

        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setTokenHash(newRefreshTokenHash); // storing hashed token
        refreshTokenObj.setUserId(stored.getUserId());
        Instant now = Instant.now();
        refreshTokenObj.setCreatedAt(now);
        refreshTokenObj.setExpiresAt(now.plusSeconds(refreshTokenExpirationSeconds));
        refreshTokenObj.setStatus(RefreshTokenStatus.ACTIVE);
        refreshTokenRepository.save(refreshTokenObj);

        // create jwt access token
        // get user details
        User user = userRepository.findById(stored.getUserId()).orElse(null);
        if(user == null){
            throw new InvalidUserDataException("User not found");
        }
        final String newAccessToken = jwtUtil.generateJwtToken(user);

        // sending 'Record' containing 'RefreshResponseDTO' object and newRefreshToken and expiration of refresh token
        return new RefreshResponseRecord( RefreshResponseDTO.from(newAccessToken, "Bearer", jwtUtil.getExpirationTimeInSeconds()), newRefreshToken, refreshTokenExpirationSeconds );
    }

    @Transactional
    public void logout(String token){
        if(token == null){ return; }
        // hash the refresh token
        String refreshTokenHash = refreshTokenUtil.generateRefreshTokenHash(token);
        // see if token is present in db
        RefreshToken stored = refreshTokenRepository.findByTokenHash(refreshTokenHash).orElse(null);
        // if token is present and status is 'ACTIVE' then we will set it to 'REVOKED' otherwise in any cases no action is needed or error to be returned
        // because final state of logout should be that make refresh token invalid
        if(stored != null && stored.getStatus() == RefreshTokenStatus.ACTIVE){
            stored.setStatus(RefreshTokenStatus.REVOKED); // since 'stored' is JPA managed entity, it will be updated in db automatically at the end of transaction
        }
    }
}

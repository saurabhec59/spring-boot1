package com.tl.spring_boot1.service;


import com.tl.spring_boot1.dto.auth.LoginRequestDTO;
import com.tl.spring_boot1.dto.auth.LoginResponseRecord;
import com.tl.spring_boot1.dto.auth.RefreshResponseRecord;
import com.tl.spring_boot1.dto.users.UserResponse;
import com.tl.spring_boot1.exception.AuthenticationException;
import com.tl.spring_boot1.exception.InvalidUserDataException;
import com.tl.spring_boot1.model.RefreshToken;
import com.tl.spring_boot1.model.RefreshTokenStatus;
import com.tl.spring_boot1.model.User;
import com.tl.spring_boot1.repository.RefreshTokenRepository;
import com.tl.spring_boot1.repository.UserRepository;
import com.tl.spring_boot1.support.LoginRequestDTOBuilder;
import com.tl.spring_boot1.support.RefreshTokenBuilder;
import com.tl.spring_boot1.support.UserMother;
import com.tl.spring_boot1.util.JwtUtil;
import com.tl.spring_boot1.util.RefreshTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private final Integer  refreshTokenExpirationSeconds = 604800;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenUtil refreshTokenUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    @BeforeEach
    public void authServiceInitialization(){
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil, refreshTokenUtil, refreshTokenRepository, refreshTokenService, refreshTokenExpirationSeconds);
    }

    // Tests for login()
    @Nested
    class Login{
        @Captor
        private ArgumentCaptor<RefreshToken> refreshTokenArgumentCaptor;

        LoginRequestDTO request = LoginRequestDTOBuilder.aLoginRequestDTO().build();

        @Test
        void loginShouldReturnCorrectValuesInLoginResponseRecord(){
            User user = UserMother.aUser();

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtUtil.generateJwtToken(user)).thenReturn("header.payload.signature");
            when(refreshTokenUtil.generateRefreshToken()).thenReturn("refreshToken");
            when(refreshTokenUtil.generateRefreshTokenHash("refreshToken")).thenReturn("refreshTokenHash");
            when(jwtUtil.getExpirationTimeInSeconds()).thenReturn(3600);

            LoginResponseRecord response = authService.login(request);

            // values passed as payload
            assertEquals("header.payload.signature", response.loginResponseDTO().getToken());
            assertEquals("Bearer", response.loginResponseDTO().getTokenType());
            assertEquals(3600, response.loginResponseDTO().getExpiresIn());
            // varify that same users details are returned after login
            assertEquals(user.getId(), response.loginResponseDTO().getUser().getId());
            assertEquals(user.getName(), response.loginResponseDTO().getUser().getName());
            assertEquals(user.getAge(), response.loginResponseDTO().getUser().getAge());
            assertEquals(user.getEmail(), response.loginResponseDTO().getUser().getEmail());
            assertEquals(user.getCity(), response.loginResponseDTO().getUser().getCity());
            assertEquals(user.getRole(), response.loginResponseDTO().getUser().getRole());
            assertInstanceOf(UserResponse.class, response.loginResponseDTO().getUser());

            // values passed in header
            assertEquals("refreshToken", response.refreshToken());
            assertEquals(refreshTokenExpirationSeconds, response.expiresIn());
        }

        @Test
        void loginShouldSaveHashedRefreshToken(){
            User user = UserMother.aUser();

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtUtil.generateJwtToken(user)).thenReturn("header.payload.signature");
            when(refreshTokenUtil.generateRefreshToken()).thenReturn("refreshToken");
            when(refreshTokenUtil.generateRefreshTokenHash("refreshToken")).thenReturn("refreshTokenHash");
            when(jwtUtil.getExpirationTimeInSeconds()).thenReturn(3600);

            LoginResponseRecord response = authService.login(request);

            verify(refreshTokenRepository).save(refreshTokenArgumentCaptor.capture());
            RefreshToken valueToBeSaved = refreshTokenArgumentCaptor.getValue();
            assertEquals("refreshTokenHash", valueToBeSaved.getTokenHash()); // important
            // optionals
            assertEquals(user.getId(), valueToBeSaved.getUserId());
            assertEquals(Duration.ofSeconds(refreshTokenExpirationSeconds), Duration.between(valueToBeSaved.getCreatedAt(), valueToBeSaved.getExpiresAt()));
            assertEquals(RefreshTokenStatus.ACTIVE, valueToBeSaved.getStatus());
        }

        @Test
        void loginShouldThrowExceptionWhenUserWithEmailDoesNotExist(){
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

            assertThrows(InvalidUserDataException.class, () -> {
                authService.login(request);
            });
        }

        @Test
        void loginShouldThrowExceptionWhenUserWithPasswordDoesNotMatch(){
            User user = UserMother.aUser();

            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

            assertThrows(InvalidUserDataException.class, () -> {
                authService.login(request);
            });
        }
    }

    @Nested
    class RefreshTokenTest{

        @Captor
        private ArgumentCaptor<RefreshToken> refreshTokenArgumentCaptor;

        String refreshToken = "refreshToken";
        String refreshTokenHash = "refreshTokenHash";
        String newRefreshToken = "newRefreshToken";
        String newRefreshTokenHash = "newRefreshTokenHash";
        String newAccessToken = "header.payload.signature";
        Integer expiresIn = 3600;
        User user = UserMother.aUser(); // user have id = 1 and refreshTokenBuilder returned 'RefreshToken' object have also 'userId' = 1
        RefreshToken stored = RefreshTokenBuilder.aRefreshToken().withTokenHash(refreshTokenHash).build();

        @Test
        void refreshShouldReturnCorrectRefreshResponseRecordWhenRefreshTokenIsValid(){

            refreshStubHelper();
            RefreshResponseRecord response = authService.refresh(refreshToken);

            // verify payload (new jwt access token)
            assertEquals(newAccessToken, response.refreshResponseDTO().getToken());
            assertEquals("Bearer", response.refreshResponseDTO().getTokenType());
            assertEquals(expiresIn, response.refreshResponseDTO().getExpiresIn());

            // Verify header values new refresh token and expiry
            assertEquals(newRefreshToken, response.newRefreshToken());
            assertEquals(refreshTokenExpirationSeconds, response.expiresIn());
        }

        @Test
        void refreshShouldSaveNewlyRefreshToken(){

            refreshStubHelper();
            authService.refresh(refreshToken);

            verify(refreshTokenRepository).save(refreshTokenArgumentCaptor.capture());
            RefreshToken valuesToBeSaved = refreshTokenArgumentCaptor.getValue();

            assertEquals(newRefreshTokenHash, valuesToBeSaved.getTokenHash()); // important
            assertEquals(stored.getUserId(), valuesToBeSaved.getUserId());
            assertEquals(Duration.ofSeconds(refreshTokenExpirationSeconds), Duration.between(valuesToBeSaved.getCreatedAt(), valuesToBeSaved.getExpiresAt()));
            assertEquals(RefreshTokenStatus.ACTIVE, valuesToBeSaved.getStatus());
        }

        @Test
        void refreshShouldSetStatusOfOldValidatedTokenToRotated(){

            refreshStubHelper();
            authService.refresh(refreshToken);

            assertEquals(RefreshTokenStatus.ROTATED, stored.getStatus());
        }

        @Test
        void refreshShouldThrowExceptionWhenRefreshTokenIsNotFound(){

            when(refreshTokenUtil.generateRefreshTokenHash(refreshToken)).thenReturn(refreshTokenHash);
            when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.empty());

            assertThrows(AuthenticationException.class, () -> {
                authService.refresh(refreshToken);
            });
        }

        @Test
        void refreshShouldThrowExceptionWhenRefreshTokenIsRotated(){
            // Additionally verify that revokeAllForUser() is called with correct userId
            RefreshToken rotated = RefreshTokenBuilder.aRefreshToken().withTokenHash(refreshTokenHash).withStatus(RefreshTokenStatus.ROTATED).build();

            when(refreshTokenUtil.generateRefreshTokenHash(refreshToken)).thenReturn(refreshTokenHash);
            when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(rotated));

            assertThrows(AuthenticationException.class, () -> {
                authService.refresh(refreshToken);
            });

            verify(refreshTokenService).revokeAllForUser(rotated.getUserId());

        }

        @Test
        void refreshShouldThrowExceptionWhenRefreshTokenIsRevoked(){
            // Additionally verify that revokeAllForUser() is called with correct userId
            RefreshToken revoked = RefreshTokenBuilder.aRefreshToken().withTokenHash(refreshTokenHash).withStatus(RefreshTokenStatus.REVOKED).build();

            when(refreshTokenUtil.generateRefreshTokenHash(refreshToken)).thenReturn(refreshTokenHash);
            when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(revoked));

            assertThrows(AuthenticationException.class, () -> {
                authService.refresh(refreshToken);
            });

            verify(refreshTokenService).revokeAllForUser(revoked.getUserId());

        }

        @Test
        void refreshShouldThrowExceptionWhenRefreshTokenIsExpired(){
            // here we are making explicitly expiry < creation
            RefreshToken expired = RefreshTokenBuilder.aRefreshToken().withTokenHash(refreshTokenHash).withCreatedAt(Instant.now().plusSeconds(60)).withExpiresAt(Instant.now()).build();

            when(refreshTokenUtil.generateRefreshTokenHash(refreshToken)).thenReturn(refreshTokenHash);
            when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(expired));

            assertThrows(AuthenticationException.class, () -> {
                authService.refresh(refreshToken);
            });

            verify(refreshTokenService, never()).revokeAllForUser(any()); // revoke all should not be called when token is only expired.
        }

        @Test // Claude Review this one explicitly that with this much duplication, is it worth to have this test? because User id and refresh token are in relationship and if user does not exist then simply it's refresh token as well as per db constraint I guess
        void refreshShouldThrowExceptionWhenUserDoesNotExist(){

            when(refreshTokenUtil.generateRefreshTokenHash(refreshToken)).thenReturn(refreshTokenHash);
            when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(stored));
            when(refreshTokenUtil.generateRefreshToken()).thenReturn(newRefreshToken);
            when(refreshTokenUtil.generateRefreshTokenHash(newRefreshToken)).thenReturn(newRefreshTokenHash);
            when(userRepository.findById(stored.getUserId())).thenReturn(Optional.empty());

            assertThrows(InvalidUserDataException.class, () -> {
                authService.refresh(refreshToken);
            });
        }

        private void refreshStubHelper(){
            when(refreshTokenUtil.generateRefreshTokenHash(refreshToken)).thenReturn(refreshTokenHash);
            when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(stored));
            when(refreshTokenUtil.generateRefreshToken()).thenReturn(newRefreshToken);
            when(refreshTokenUtil.generateRefreshTokenHash(newRefreshToken)).thenReturn(newRefreshTokenHash);
            when(userRepository.findById(stored.getUserId())).thenReturn(Optional.of(user));
            when(jwtUtil.generateJwtToken(user)).thenReturn(newAccessToken);
            when(jwtUtil.getExpirationTimeInSeconds()).thenReturn(expiresIn);
        }
    }

    @Nested
    class LogoutTest{

        String refreshToken = "refreshToken";
        String refreshTokenHash = "refreshTokenHash";
        RefreshToken stored = RefreshTokenBuilder.aRefreshToken().withTokenHash(refreshTokenHash).build();

        @Test
        void logoutShouldSucceedWhenTokenIsValid(){
            // verify that token is revoked as well
            when(refreshTokenUtil.generateRefreshTokenHash(refreshToken)).thenReturn(refreshTokenHash);
            when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(stored));

            assertDoesNotThrow(() -> {
                authService.logout(refreshToken);
            });
            assertEquals(RefreshTokenStatus.REVOKED, stored.getStatus());
        }

        @Test
        void logoutShouldSucceedWhenTokenIsNull(){ // Idempotent
            // also verify that hash conversion &  db lookup not performed
            assertDoesNotThrow(() -> {
                authService.logout(null);
            });

            verify(refreshTokenUtil, never()).generateRefreshTokenHash(anyString());
            verify(refreshTokenRepository, never()).findByTokenHash(anyString());
        }

        @Test
        void logoutShouldSucceedTokenIsRotated(){ // Idempotent
            RefreshToken rotated = RefreshTokenBuilder.aRefreshToken().withTokenHash(refreshTokenHash).withStatus(RefreshTokenStatus.ROTATED).build();
            when(refreshTokenUtil.generateRefreshTokenHash(refreshToken)).thenReturn(refreshTokenHash);
            when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(rotated));

            assertDoesNotThrow(() -> {
                authService.logout(refreshToken);
            });
            // since token status is ROTATED so it will not change status as not required
            assertEquals(RefreshTokenStatus.ROTATED, rotated.getStatus());
        }

        @Test
        void logoutShouldSucceedTokenIsRevoked(){ // Idempotent
            RefreshToken revoked = RefreshTokenBuilder.aRefreshToken().withTokenHash(refreshTokenHash).withStatus(RefreshTokenStatus.REVOKED).build();
            when(refreshTokenUtil.generateRefreshTokenHash(refreshToken)).thenReturn(refreshTokenHash);
            when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(revoked));

            assertDoesNotThrow(() -> {
                authService.logout(refreshToken);
            });
            // since token status is REVOKED so it will not change status as not required
            assertEquals(RefreshTokenStatus.REVOKED, revoked.getStatus());
        }
    }
}

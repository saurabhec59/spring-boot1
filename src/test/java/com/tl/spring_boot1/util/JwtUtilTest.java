package com.tl.spring_boot1.util;

import com.tl.spring_boot1.model.User;
import com.tl.spring_boot1.support.UserMother;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {
    private static final String SECRET_KEY1 = "dGhpcy1pcy1hLXRlc3Qtc2VjcmV0LWtleS0zMi1ieXRlcyEh";
    private static final String SECRET_KEY2 = "c29tZS1vdGhlci10ZXN0LXNlY3JldC1rZXktMzItYnl0ZXMhIQ";
    private static final Integer EXPIRATION_SECONDS = 3600;
    private static final Integer EXPIRATION_SECONDS2 = -1; // to expire token immediately

    JwtUtil jwtUtil = new JwtUtil(SECRET_KEY1,  EXPIRATION_SECONDS);

    // Testing generateJwtToken()
    @Test
    void shouldNotReturnNullToken(){
        // get test user
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        assertNotNull(jwtToken);
    }

    @Test
    void shouldReturnThreeDotSeparatedValidParts(){
        // get test user
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        String[] parts = jwtToken.split("\\.");
        assertEquals(3, parts.length);
        assertFalse(parts[0].isBlank());
        assertFalse(parts[1].isBlank());
        assertFalse(parts[2].isBlank());
    }

    @Test
    void shouldContainSameUserIdInPayload(){
        // get test user
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        // parse and extract userId
        Claims claims = getClaimsFromToken(jwtToken, SECRET_KEY1);
        Integer userId = Integer.parseInt(claims.get("sub", String.class));

        assertEquals(user.getId(), userId);
    }

    @Test
    void shouldContainSameEmailInPayload(){
        // get test user
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        // parse and extract userId
        Claims claims = getClaimsFromToken(jwtToken, SECRET_KEY1);
        String email = claims.get("email", String.class);

        assertEquals(user.getEmail(), email);
    }

    @Test
    void shouldContainSameRoleInPayload(){
        // get test user
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        Claims claims = getClaimsFromToken(jwtToken, SECRET_KEY1);
        String role = claims.get("role", String.class);

        assertEquals(user.getRole(), role);
    }

    @Test
    void shouldNotContainPasswordInPayload(){
        // get test user
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        // parse and extract userId
        Claims claims = getClaimsFromToken(jwtToken, SECRET_KEY1);
        String password = claims.get("password", String.class);
        assertNull(password);
    }

    @Test
    void shouldParseSuccessfullyWithSameSecretKey(){
        // get test user
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        // parsing with same secret key
        assertDoesNotThrow(() -> {
            getClaimsFromToken(jwtToken, SECRET_KEY1); // passing same key which is used while creating
        });
    }

    @Test
    void shouldThrowExceptionWhenParsedWithDifferentSecretKey(){
        // get test user
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        assertThrows(SignatureException.class, () -> {
            getClaimsFromToken(jwtToken, SECRET_KEY2);// Passing 2nd secret key
        });
    }

    // Testing validateToken
    @Test
    void shouldReturnTrueWhenTokenIsValid(){
        // get test user
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        boolean result = jwtUtil.validateToken(jwtToken);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenTokenIsMalformed(){
        // get test user
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        // tamper the jwtToken
        jwtToken = jwtToken.replace(".", "-");
        boolean result = jwtUtil.validateToken(jwtToken);
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenTokenIsExpired(){
        User user = UserMother.aUser();
        // create a new instance of 'JwtUtil' with -1 second expiry
        JwtUtil jwtUtil = new JwtUtil(SECRET_KEY1,EXPIRATION_SECONDS2); // local takes precedence
        String jwtToken = jwtUtil.generateJwtToken(user);
        boolean result = jwtUtil.validateToken(jwtToken);
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenSignatureIsTampered(){
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);// this token is created with SECRET_KEY1
        // now creating a new instance of 'JwtUtil' with SECRET_KEY2 and validating the token
        JwtUtil jwtUtil2 = new JwtUtil(SECRET_KEY2,EXPIRATION_SECONDS);
        boolean result = jwtUtil2.validateToken(jwtToken); // this will try to verify with SECRET_KEY2
        assertFalse(result);
    }

    // Testing getIdFromToken()
    @Test
    void shouldReturnCorrectUserIdFromToken(){
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        Integer returnedId = jwtUtil.getIdFromToken(jwtToken);
        assertEquals(user.getId(), returnedId);
    }

    // Testing getEmailFromToken()
    @Test
    void shouldReturnCorrectEmailFromToken(){
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        String returnedEmail = jwtUtil.getEmailFromToken(jwtToken);
        assertEquals(user.getEmail(), returnedEmail);
    }

    // Testing getRoleFromToken()
    @Test
    void shouldReturnCorrectRoleFromToken(){
        User user = UserMother.aUser();
        String jwtToken = jwtUtil.generateJwtToken(user);
        String returnedRole = jwtUtil.getRoleFromToken(jwtToken);
        assertEquals(user.getRole(), returnedRole);
    }

    // Testing getExpirationTimeInSeconds()
    @Test
    void shouldReturnCorrectExpirationTimeInSeconds(){
        assertEquals(EXPIRATION_SECONDS, jwtUtil.getExpirationTimeInSeconds());
    }

    // helper methods

    // parseSignedClaims() throws error so caller needs to handle
    Claims getClaimsFromToken(String token, String secretKey){
        return Jwts.parser()
                .verifyWith(getTestSecretKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    SecretKey getTestSecretKey(String base64EncodedSecretKey) {
        byte[] rawSecretKey = Decoders.BASE64.decode(base64EncodedSecretKey);
        return Keys.hmacShaKeyFor(rawSecretKey);
    }
}

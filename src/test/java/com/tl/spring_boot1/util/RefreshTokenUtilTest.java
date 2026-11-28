package com.tl.spring_boot1.util;

import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RefreshTokenUtilTest {
    final String URL_SAFE_PATTERN = "^[a-zA-Z0-9_-]+$";
    RefreshTokenUtil refreshTokenUtil = new RefreshTokenUtil();

    /*
        => generateRefreshToken() <=
        - should not return a null string token
        - generateRefreshToken() is converting 32 bytes array to Base64 encoded string which is 43 characters long.
        - returned string should be URL safe and should only contain [a-z, A-Z, 0-9, -, _]
        - after decoding the returned Base64url encoded string, it should be same byte array of '32' bytes.
        - generateRefreshToken() should return unique strings
     */

    @Test
    public void testGenerateRefreshToken(){
        String refreshToken = refreshTokenUtil.generateRefreshToken();

        assertNotNull(refreshToken);
        assertEquals(43, refreshToken.length());
        assertTrue(refreshToken.matches(URL_SAFE_PATTERN));

        // decoding into bytes again
        byte[] bytes = Base64.getUrlDecoder().decode(refreshToken);
        assertEquals(32, bytes.length);

        // checking uniqueness of generated refresh token by generating 1000 tokens and checking if all are unique
        Set<String> set = new HashSet<>();
        for(int i=1; i<=1000; i++){
            set.add(refreshTokenUtil.generateRefreshToken());
        }

        assertEquals(1000, set.size());
    }

    /*
        => generateRefreshTokenHash() <=
        - should not return a null string token
        - raw refresh token should not be same as hashed token
        - returned hashed string will be always of length 43
            because we used sha-256 which always produces 256 bits array regardless of i/p size and when we convert
            this byte array into Base64 url encoded string as "Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes)"
            then returned string is always of 43 length.
        - hashing must be deterministic: calling same hash method on same input multiple times should produce same hashed string
        - Different output should produce different hashed string
        - should throw 'IllegalArgumentException' when null or empty string as refreshToken is passed to hash
     */

    @Test
    public void testGenerateRefreshTokenHash(){
        String refreshToken1 = refreshTokenUtil.generateRefreshToken();
        String refreshTokenHash1 =  refreshTokenUtil.generateRefreshTokenHash(refreshToken1);

        assertNotNull(refreshTokenHash1);
        assertNotEquals(refreshToken1, refreshTokenHash1);
        assertEquals(43, refreshTokenHash1.length());

        // checking deterministic behaviour
        Set<String> set = new HashSet<>();
        for(int i=1; i<=5; i++){
            set.add(refreshTokenUtil.generateRefreshTokenHash(refreshToken1));
        }
        assertEquals(1, set.size());

        String refreshToken2 = refreshTokenUtil.generateRefreshToken();
        String refreshTokenHash2 =  refreshTokenUtil.generateRefreshTokenHash(refreshToken2);
        assertNotEquals(refreshTokenHash1, refreshTokenHash2);

        assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenUtil.generateRefreshTokenHash(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenUtil.generateRefreshTokenHash("");
        });
    }

}

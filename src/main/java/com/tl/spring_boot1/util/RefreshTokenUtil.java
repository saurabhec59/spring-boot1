package com.tl.spring_boot1.util;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RefreshTokenUtil {
    /*SecureRandom is a java class used to generate cryptographically secure random number.
    It is expensive on cpu to initialize 'SecureRandom' each time a new login happens. so we will create once and reuse it.
    Since class is declared @Component so by default spring will create only one instance of it. it's method nexBytes() is thread safe as well.
    It's method nextBytes() generates random raw bytes like [11110101, 01010101,....] and stores in provided byte array.
    Here we passed byte[] of 32 bytes size so nextBytes() will generate 32 raw bytes and will store them in 'bytes' array.
    Then we are converting those raw bytes into String for convenient transfer and storage.
    */

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateRefreshToken(){
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return  Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    /*
     a URL-safe base 64 encoded string contains only [a-z], [A-Z], [0-9], [-], [_] only. Nothing else
      and getUrlEncoder() make sure that only these characters are there in encoded string.
      withoutPadding() make sure that '=' padding characters are not there in encoded string.
    */

    public String generateRefreshTokenHash(String refreshToken){
        // Assert.hasText() provided by spring throws 'IllegalArgumentException' if string do not contain any actual text.
        // and since in our GlobalException handler we are not handling directly this error or 'RuntimeException' so spring will
        // send 500 internal error and this message will not be sent to client.
        Assert.hasText(refreshToken, "Refresh token can't be null or empty");
        try{
            // convert String into bytes because Hashing algos works on bytes and returns bytes as well.
            // let's say if refresh token was "ABC" then below line will convert it into [65, 66, 67]
            byte[] bytes = refreshToken.getBytes(StandardCharsets.UTF_8); // convert String into bytes because Hashing algos works on raw bytes and returns raw bytes as well
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(bytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes); // can be converted to Hex as well.
        }catch(Exception e){
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

package com.tl.spring_boot1.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseBusinessException {
    public AuthenticationException(String message){ super(message, HttpStatus.UNAUTHORIZED.value(), "Unauthorized"); }
}

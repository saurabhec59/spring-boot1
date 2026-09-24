package com.tl.spring_boot1.exception;

import org.springframework.http.HttpStatus;

public class InvalidUserDataException extends BaseBusinessException{
    public InvalidUserDataException(String message){
        super(message, HttpStatus.BAD_REQUEST.value(), "Bad Request");
    }
}

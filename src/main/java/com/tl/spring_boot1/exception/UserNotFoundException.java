package com.tl.spring_boot1.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseBusinessException{
    public UserNotFoundException(Integer id){
        super("User with id " + id + " not found", HttpStatus.NOT_FOUND.value(), "Not Found");
    }

}

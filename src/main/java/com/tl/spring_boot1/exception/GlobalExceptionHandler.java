package com.tl.spring_boot1.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.tl.spring_boot1.dto.ErrorResponse;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseBusinessException.class)
    public ResponseEntity<ErrorResponse> userNotFoundExceptionHandler(BaseBusinessException e, HttpServletRequest request){

        String currentTime = LocalDateTime.now().toString();

        ErrorResponse response = new ErrorResponse( currentTime, e.getStatusCode(), e.getError(), e.getMessage(), request.getRequestURI() );
        return ResponseEntity.status(e.getStatusCode()).body( response );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, HttpServletRequest request){

        String currentTime = LocalDateTime.now().toString();

        // create a Map to store all the field names and rejected values respectively
        Map<String, String> map = new HashMap<>();

        // Get the List of all validation errors
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        // Loop through this List & cast each Object in it into 'FieldError' to Use methods ('getField()' & 'getRejectedValue') provided by 'FieldError' class. And store in Map
        for(ObjectError error : errors){
            FieldError fieldError = (FieldError)error;
            map.put( fieldError.getField(), fieldError.getDefaultMessage() );
        }
        ErrorResponse response = new ErrorResponse( currentTime, HttpStatus.BAD_REQUEST.value(), "Bad Request", "Validation failed", map,  request.getRequestURI() );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( response );

    }
}
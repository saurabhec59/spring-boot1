package com.tl.spring_boot1.dto;

import java.util.Map;

public class ErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> validationErrors;
    private String path;

    public ErrorResponse(String timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // This constructor will be called by methodArgumentNotValidExceptionHandler to include the list of all validation errors
    public ErrorResponse(String timestamp, int status, String error, String message, Map<String, String> validationErrors, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.validationErrors = validationErrors;
        this.path = path;
    }

    // getters so that spring can convert this object into json while sending to client
    public String getTimestamp() {
        return timestamp;
    }
    public int getStatus() {
        return status;
    }
    public String getError() {
        return error;
    }
    public String getMessage() {
        return message;
    }
    public String getPath() {
        return path;
    }
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}

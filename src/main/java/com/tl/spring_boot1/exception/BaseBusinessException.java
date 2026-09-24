package com.tl.spring_boot1.exception;

public abstract class BaseBusinessException extends RuntimeException{
    public BaseBusinessException(String message, int statusCode, String error){
        super(message);
        this.statusCode = statusCode;
        this.error = error;
    }

    private int statusCode;
    private String error; // like " Not Found", "Bad Request" etc

    // public getters
    public int getStatusCode() {
        return statusCode;
    }
    public String getError() {
        return error;
    }
}

/*
    Each error response needs to send :
        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        but out of these 5, but initialization of fields like 'status', 'error', 'message' are the responsibility of those
        custom error classes like 'UserNotFoundException'.
        So one way is to declare these fields in each custom error classes but that will be repetitive for each class code and also
        in the 'GlobalExceptionHandler' while declaring handlers for those methods:
        We will have to do like:
        ===>  for errors thrown as new UserNotFoundException(....),
        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> userNotFoundExceptionHandler(UserNotFoundException e, HttpServletRequest request){ ....}

        ===>  for errors thrown as new ProductNotFoundException(....),
        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ErrorResponse> productNotFoundExceptionHandler(ProductNotFoundException e, HttpServletRequest request){ ....}

        Another way is to let error handler methods in Global file construct them,
        this way custom error classes will not have same repetitive code issue but now these handlers will have the same problem.

        Solution:
        We define these common fields like 'status', 'error', 'message' in the "BASE BUSINESS EXCEPTION" class and then all
        the custom error classes will extend this base class and will have to provide these fields values in their constructor.

        This way there will need only one exception handler method in 'GlobalExceptionHandler' class which will handle all
        the custom error classes and will construct the "ErrorResponse" object from DTO
*/

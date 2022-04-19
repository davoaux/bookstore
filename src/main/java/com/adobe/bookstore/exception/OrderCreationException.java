package com.adobe.bookstore.exception;

import org.springframework.http.HttpStatus;

public class OrderCreationException extends Exception {
    private HttpStatus httpStatus;

    public OrderCreationException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}

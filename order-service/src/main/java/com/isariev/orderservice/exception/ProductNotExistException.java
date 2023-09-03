package com.isariev.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotExistException extends RuntimeException {
    public ProductNotExistException() {
    }

    public ProductNotExistException(String message) {
        super(message);
    }

    public ProductNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductNotExistException(Throwable cause) {
        super(cause);
    }

    public ProductNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

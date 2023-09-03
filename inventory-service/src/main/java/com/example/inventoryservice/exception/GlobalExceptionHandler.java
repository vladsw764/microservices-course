package com.example.inventoryservice.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.net.ConnectException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConnectException.class)
    public ProblemDetail handleConnectException(ConnectException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), ex.getMessage());
        problemDetail.setTitle("refused connection to db...");
        return problemDetail;
    }

    @ExceptionHandler(ProductNotExistException.class)
    public ProblemDetail handleProductNotExistException(ProductNotExistException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), ex.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ProblemDetail handleConnectException(HttpServerErrorException.InternalServerError ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), ex.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ProblemDetail handleBadRequest(HttpClientErrorException.BadRequest ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.getMessage());
    }
}
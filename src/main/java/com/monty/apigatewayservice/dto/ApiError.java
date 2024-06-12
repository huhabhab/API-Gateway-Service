package com.monty.apigatewayservice.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiError {

    private HttpStatus status;
    private LocalDateTime timestamp;
    private String exception;
    private String message;


    private ApiError() {

    }

    public ApiError(HttpStatus status, Exception ex) {
        this();
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.exception = ex.getClass().getSimpleName();
        this.message = ex.getLocalizedMessage();
    }
}

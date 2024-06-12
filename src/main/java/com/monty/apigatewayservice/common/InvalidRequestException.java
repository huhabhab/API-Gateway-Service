package com.monty.apigatewayservice.common;

import lombok.Data;
import org.springframework.http.HttpStatus;
/**
 * Custom exception class for representing invalid requests.
 *
 * <p>This exception is thrown when an invalid request is detected. It includes details such as
 * the HTTP status code and additional details about the invalid request.
 *
 * <p>{@code InvalidRequestException} is a subclass of {@code Exception}.
 */
@Data
public class InvalidRequestException extends Exception {
    private HttpStatus status;
    private String additionalDetails;

    public InvalidRequestException(String message, HttpStatus status, String additionalDetails) {

        super(message);
        this.status = status;
        this.additionalDetails = additionalDetails;
    }
}
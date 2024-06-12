package com.monty.apigatewayservice.common;

import com.monty.apigatewayservice.dto.ApiError;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * GlobalExceptionHandler class handles exceptions globally for the API Gateway service.
 * It provides centralized exception handling and response generation.
 */
@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles general exceptions and generates an appropriate API error response.
     *
     * @param ex The exception to be handled.
     * @return ResponseEntity containing the API error response.
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ApiError> UnhandledExceptions(Exception ex) {
        log.error(ex.getLocalizedMessage());
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    /**
     * Handles HttpClientErrorException and generates a NOT_FOUND API error response.
     *
     * @param ex The HttpClientErrorException to be handled.
     * @return ResponseEntity containing the API error response.
     */
    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleHttpClientErrorException(HttpClientErrorException ex) {
        log.error(ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    /**
     * Handles ResourceAccessException and generates a SERVICE_UNAVAILABLE API error response.
     *
     * @param ex The ResourceAccessException to be handled.
     * @return ResponseEntity containing the API error response.
     */
    @ExceptionHandler(ResourceAccessException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleResourceAccessException(ResourceAccessException ex) {
        log.error(ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE, ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(apiError);
    }

    /**
     * Exception handler for RateLimitExceededException. Handles the exception,
     * logs the error message, creates an {@link ApiError} response, and returns
     * a ResponseEntity with a 429 Too Many Requests status.
     *
     * @param ex The RateLimitExceededException instance.
     * @return ResponseEntity containing the ApiError with a 429 status.
     */
    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleRateLimitExceededException(RateLimitExceededException ex) {
        log.error(ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.TOO_MANY_REQUESTS, ex);

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(ex.getHeaders())
                .body(apiError);
    }

    /**
     * Exception handler for InvalidRequestException. Handles the exception,
     * logs the error message, creates an {@link ApiError} response, and returns
     * a ResponseEntity with a 400 Bad Request status.
     *
     * @param ex The InvalidRequestException instance.
     * @return ResponseEntity containing the ApiError with a 400 status.
     */
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleInvalidRequestException(InvalidRequestException ex) {
        log.error(ex.getMessage());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

}

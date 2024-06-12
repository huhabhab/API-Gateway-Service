package com.monty.apigatewayservice.common;

import lombok.Data;
import org.springframework.http.HttpHeaders;
/**
 * Exception indicating that the rate limit for API requests has been exceeded.
 *
 * <p>This exception is thrown when a client has exhausted their allocated API request quota.
 *
 * <p>{@code RateLimitExceededException} is a subclass of {@code RuntimeException}.
 */
@Data
public class RateLimitExceededException extends RuntimeException {

    private HttpHeaders headers;

    public RateLimitExceededException(HttpHeaders headers) {
        super("You have exhausted your API Request Quota");
        this.headers = headers;
    }

}

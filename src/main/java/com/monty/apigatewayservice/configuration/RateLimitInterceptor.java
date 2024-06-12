package com.monty.apigatewayservice.configuration;

import com.monty.apigatewayservice.common.InvalidRequestException;
import com.monty.apigatewayservice.common.RateLimitExceededException;
import com.monty.apigatewayservice.service.RatePlanService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Interceptor for rate limiting API requests based on the provided API key.
 *
 * <p>This interceptor checks the rate limit for each API request using the provided API key.
 * If the rate limit is exceeded, it adds appropriate headers to the response and throws a
 * {@link RateLimitExceededException}.
 */
@Component
@Log4j2
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String HEADER_API_KEY = "X-api-key";
    private static final String HEADER_LIMIT_REMAINING = "X-Rate-Limit-Remaining";
    private static final String HEADER_RETRY_AFTER = "X-Rate-Limit-Retry-After-Milliseconds";

    @Autowired
    private RatePlanService ratePlanService;

    /**
     * Intercepts the request before it reaches the controller to check for rate limits.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @param handler  The handler (controller method).
     * @return {@code true} if the request should proceed; {@code false} otherwise.
     * @throws Exception If an error occurs during rate limit checking.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String correlationToken = UUID.randomUUID().toString();
        request.setAttribute("correlationToken", correlationToken);

        logRequestDetails(request, correlationToken);

        String apiKey = request.getHeader(HEADER_API_KEY);
        HttpHeaders headers = new HttpHeaders();

        if (apiKey == null || apiKey.isEmpty()) {
            throw new InvalidRequestException("Missing Header: " + HEADER_API_KEY + " - " + request.getServerName(), HttpStatus.BAD_REQUEST, request.getScheme());

        }

        Bucket tokenBucket = ratePlanService.resolveBucket(apiKey);

        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {

            response.addHeader(HEADER_LIMIT_REMAINING, String.valueOf(probe.getRemainingTokens()));
            return true;

        } else {

            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000;
            headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
            headers.add(HEADER_RETRY_AFTER, String.valueOf(waitForRefill));
            throw new RateLimitExceededException(headers);

        }
    }

    private void logRequestDetails(HttpServletRequest request, String correlationToken) {
        // Log the request details
        log.info("Request received - Correlation token: {}, Method: {}, URI: {}, Headers: {}",
                correlationToken,
                request.getMethod(),
                request.getRequestURI(),
                getRequestHeaders(request));
    }

    private String getRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headers = new StringBuilder();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            headers.append(headerName)
                    .append(": ")
                    .append(headerValue)
                    .append(", ");
        }

        // Remove the trailing comma and space
        int length = headers.length();
        if (length > 0) {
            headers.delete(length - 2, length);
        }

        return headers.toString();
    }


}
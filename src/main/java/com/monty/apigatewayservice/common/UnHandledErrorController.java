package com.monty.apigatewayservice.common;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UnHandledErrorController class handles unhandled errors and provides a generic error response.
 * It implements the ErrorController interface for custom error handling.
 */
@RestController
public class UnHandledErrorController implements ErrorController {

    /**
     * Handles the "/error" endpoint and throws a generic exception with an error message.
     *
     * @throws Exception Generic exception with an error message.
     */
    @RequestMapping("/error")
    public void handleError() throws Exception {
        String errorMessage = "An error occurred. Please check the request and try again.";
        throw new Exception(errorMessage);
    }
}


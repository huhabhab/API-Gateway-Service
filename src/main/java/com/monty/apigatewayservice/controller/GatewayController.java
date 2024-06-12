package com.monty.apigatewayservice.controller;

import com.monty.apigatewayservice.service.GatewayService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;


@RestController
@Log4j2
public class GatewayController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private GatewayService gatewayService;

    /**
     * Forward the incoming request to the corresponding microservice.
     *
     * <p>This method constructs the URL for the microservice by appending the full path of the incoming request.
     * It then creates headers for the microservice request and forwards the request to the microservice.
     *
     * @param request The incoming HTTP request.
     * @return A ResponseEntity with the same Content-Type and the microservice's response body.
     * @throws IOException If an I/O error occurs while reading the request body.
     */
    @RequestMapping("/gwrl/**")
    public ResponseEntity<Object> forwardRequest(HttpServletRequest request) throws Exception {

        // Construct the URL to your microservice by appending the full path
        String microserviceUrl = gatewayService.getServiceUrl(request.getRequestURI());

        // Check if headers to be included in the respnse
        Boolean includeHeadersValue = gatewayService.getIncludeHeadersValue(request.getRequestURI());

        // Create headers for the microservice request and add all headers from the incoming request
        HttpHeaders requestHeaders = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            requestHeaders.add(headerName, request.getHeader(headerName));
        }

        // Forward the request to the microservice and return the response
        HttpEntity<?> httpEntity = HttpMethod.resolve(request.getMethod()) == HttpMethod.GET
                ? new HttpEntity<>(requestHeaders)
                : new HttpEntity<>(request.getInputStream(), requestHeaders);

        ResponseEntity<String> response = restTemplate.exchange(microserviceUrl, HttpMethod.resolve(request.getMethod()), httpEntity, String.class);

        // Extract correlationToken for tracking purpose
        String correlationToken = (String) request.getAttribute("correlationToken");

        log.info("Forwarded request to {} - correlationToken: {}, status code: {}", microserviceUrl, correlationToken, response.getStatusCode());

        // Return a ResponseEntity with the same Content-Type and the microservice's response body
        return ResponseEntity.status(response.getStatusCode())
                .headers(includeHeadersValue != null && includeHeadersValue ? response.getHeaders() : HttpHeaders.EMPTY)
                .body(response.getBody());
    }
}
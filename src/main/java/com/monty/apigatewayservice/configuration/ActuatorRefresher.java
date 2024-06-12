package com.monty.apigatewayservice.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * Component responsible for periodically refreshing the Actuator endpoint.
 *
 * <p>This component sends an HTTP POST request to the Actuator's "/actuator/refresh" endpoint
 * at a fixed rate to trigger a refresh of configurations.
 */
@Component
@Log4j2
public class ActuatorRefresher {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${management.server.port}")
    private int managementPort;

    /**
     * Scheduled method to refresh the Actuator endpoint.
     *
     * <p>This method sends an HTTP POST request to the Actuator's "/actuator/refresh" endpoint
     * to trigger a refresh of configurations.
     *
     * <p>The method is scheduled to run at a fixed rate of 60,000 milliseconds.
     *
     * @throws Exception If an error occurs during the refresh operation.
     */
    @Scheduled(fixedRate = 60000)
    public void refreshActuator() throws Exception {
        String baseUrl = "http://localhost:" + managementPort;
        String refreshUrl = baseUrl + "/actuator/refresh";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        restTemplate.postForEntity(refreshUrl, requestEntity, Void.class);
        log.info("Configs synced successfully! Actuator Refresh triggered at " + LocalDateTime.now());

    }
}



package com.monty.apigatewayservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * AppConfiguration class defines the application configuration, including the creation beans.
 */
@Configuration
public class AppConfiguration {

    /**
     * Creates and configures a RestTemplate bean for making HTTP requests.
     *
     * @return RestTemplate instance for use in the application.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

package com.monty.apigatewayservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration class for defining routes and service information.
 *
 * <p>This class is annotated with { @ConfigurationProperties("service-url")} to bind
 * configuration properties under the "service-url" prefix. It also uses { @RefreshScope}
 * to support dynamic refreshing of properties.
 */
@Data
@Component
@ConfigurationProperties
@RefreshScope
public class RouteConfiguration {

    private List<ServiceInfo> value;

    @Data
    public static class ServiceInfo {
        private String serviceName;
        private String serviceUrl;
        private boolean includeHeaders;
    }

}


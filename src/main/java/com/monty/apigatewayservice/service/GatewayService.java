package com.monty.apigatewayservice.service;

import com.monty.apigatewayservice.configuration.RouteConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class GatewayService {

    private static final String GWRL_MARKER = "gwrl";
    @Autowired
    private RouteConfiguration configuration;


    /**
     * Get the service URL for a specific service name.
     *
     * @param serviceName  The name of the service.
     * @param serviceInfos List of service information.
     * @return The URL of the service.
     * @throws MalformedURLException If the service is not found or the URL is invalid.
     */
    public String getServiceUrlByServiceName(String serviceName, List<RouteConfiguration.ServiceInfo> serviceInfos)
            throws MalformedURLException {
        Optional<String> serviceUrl = serviceInfos.stream()
                .filter(info -> serviceName.equals(info.getServiceName()))
                .map(RouteConfiguration.ServiceInfo::getServiceUrl)
                .findFirst();

        return serviceUrl.orElseThrow(() -> new MalformedURLException("Invalid or non-registered service: " + serviceName));
    }

    /**
     * Get the service URL based on the provided full path.
     *
     * @param fullPath The full path containing the service name marker.
     * @return The URL of the service.
     * @throws MalformedURLException If the service name is not found or the URL is invalid.
     */
    public String getServiceUrl(String fullPath) throws MalformedURLException {
        List<RouteConfiguration.ServiceInfo> serviceInfos = configuration.getValue();
        URI uri = URI.create(fullPath);

        // Extracting the service name from the path
        String[] pathSegments = uri.getPath().split("/");
        int serviceNameIndex = findServiceNameIndex(pathSegments);

        if (serviceNameIndex != -1 && serviceNameIndex + 1 < pathSegments.length) {
            String serviceName = pathSegments[serviceNameIndex];
            String completePath = buildCompletePath(pathSegments, serviceNameIndex + 1);

            String baseUrl = getServiceUrlByServiceName(serviceName, serviceInfos);
            return baseUrl + completePath;
        } else {
            log.error("Service name not found in the path: " + fullPath);
            throw new MalformedURLException("Invalid or non-registered service!");
        }
    }

    /**
     * Finds the index of the first occurrence of the "gwrl" marker in the given array of path segments.
     *
     * @param pathSegments An array of path segments.
     * @return The index of the "gwrl" marker if found, or -1 if not present.
     */
    private int findServiceNameIndex(String[] pathSegments) {
        for (int i = 0; i < pathSegments.length - 1; i++) {
            if (GWRL_MARKER.equals(pathSegments[i])) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Builds and returns the complete path by joining the specified path segments starting from the given index.
     *
     * @param pathSegments An array of path segments.
     * @param startIndex   The index from which to start building the complete path.
     * @return The complete path as a string.
     */
    private String buildCompletePath(String[] pathSegments, int startIndex) {
        return "/" + String.join("/", Arrays.copyOfRange(pathSegments, startIndex, pathSegments.length));
    }

    /**
     * Retrieves the 'includeHeaders' value for a specific service name based on the provided full path.
     *
     * @param fullPath The full path containing the service name marker.
     * @return The 'includeHeaders' value for the specified service name.
     * @throws MalformedURLException If the service name is not found or the URL is invalid.
     */
    public Boolean getIncludeHeadersValue(String fullPath) throws MalformedURLException {
        List<RouteConfiguration.ServiceInfo> serviceInfos = configuration.getValue();
        URI uri = URI.create(fullPath);

        // Extracting the service name from the path
        String[] pathSegments = uri.getPath().split("/");
        int serviceNameIndex = findServiceNameIndex(pathSegments);

        if (serviceNameIndex != -1 && serviceNameIndex + 1 < pathSegments.length) {
            String serviceName = pathSegments[serviceNameIndex];

            Optional<Boolean> includeHeaders = serviceInfos.stream()
                    .filter(info -> serviceName.equals(info.getServiceName()))
                    .map(RouteConfiguration.ServiceInfo::isIncludeHeaders)
                    .findFirst();

            return includeHeaders.orElseThrow(() -> new MalformedURLException("Invalid or non-registered service: " + serviceName));

        } else {
            log.error("Service name not found in the path: " + fullPath);
            throw new MalformedURLException("Invalid or non-registered service!");
        }
    }
}

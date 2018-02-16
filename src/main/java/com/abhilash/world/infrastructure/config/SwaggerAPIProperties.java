package com.abhilash.world.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Configs to overriding the Swagger properties
 *
 * @author Abhilash Ghosh
 */
@ConfigurationProperties(prefix = "swagger")
@Data
@Component
public class SwaggerAPIProperties {
    private String title;
    private String description;
    private String version;
    private String termsOfServiceUrl;
    private String license;
    private String licenseUrl;
}

package com.examsolver.preprocessor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "external.nougat")
public class NougatProperties {
    private String baseUrl;
}

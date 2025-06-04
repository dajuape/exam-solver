package com.examsolver.preprocessor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "preprocessor")
public class PreprocessorProperties {

    private Map<String, String> exerciseDelimiters;
}

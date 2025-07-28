package com.examsolver.preprocessor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "preprocessor.noise")
public class NoiseDetectionProperties {

    private double noiseRatioThreshold;
    private double malformedWordRatioThreshold;
    private int malformedWordMinLength;
    private double malformedCharRatio;

}

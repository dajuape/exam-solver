package com.examsolver.openai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;


@ConfigurationProperties(prefix = "openai")
@Data
public class OpenAiProperties {
    private String apiKey;
    private String url;
    private Model model = new Model();
    private Timeout timeout = new Timeout();
    private int maxRetries = 2;

    @Data
    public static class Model {
        private String text;
        private String vision;
        private String fallback;
    }

    @Data
    public static class Timeout {
        private Duration connect = Duration.ofSeconds(5);
        private Duration read = Duration.ofSeconds(30);
        private Duration text = Duration.ofSeconds(10);
    }
}

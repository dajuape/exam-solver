package com.examsolver.openai.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;


@ConfigurationProperties(prefix = "openai")
@Validated
@Data
public class OpenAiProperties {
    @NotBlank
    private String apiKey;
    @NotBlank
    private String url;
    @NotNull
    private Model model = new Model();
    @NotNull
    private Timeout timeout = new Timeout();

    @Min(0)
    private int maxRetries;
    @Min(1)
    private int maxConcurrent;

    @NotNull
    private Retry retry = new Retry();

    @Data
    public static class Model {
        @NotBlank
        private String text, vision, fallback;
    }

    @Data
    public static class Timeout {
        @NotNull
        private Duration connect;
        @NotNull
        private Duration read;
        @NotNull
        private Duration write;
        @NotNull
        private Duration text;
    }


    @Data
    public static class Retry {
        @NotNull
        private Duration initialDelay = Duration.ofMillis(300);
        @NotNull
        private Duration maxDelay = Duration.ofSeconds(3);
    }
}

package com.examsolver.openai.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
@Data
@RequiredArgsConstructor
public class OpenAiClientConfig {

    private final OpenAiProperties openAiProperties;

    @Bean
    public WebClient openAiWebClient() {
        return WebClient.builder()
                .baseUrl(openAiProperties.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiProperties.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

package com.examsolver.openai.service.client;


import com.examsolver.openai.config.OpenAiClientConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OpenAiClient {

    private final OpenAiClientConfig openAiClientConfig;


}


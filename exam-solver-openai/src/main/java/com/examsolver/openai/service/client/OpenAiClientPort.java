package com.examsolver.openai.service.client;

import com.examsolver.openai.dto.client.OpenAiRequest;
import com.examsolver.openai.dto.client.OpenAiResponse;
import reactor.core.publisher.Mono;

public interface OpenAiClientPort {
    Mono<OpenAiResponse> chatCompletions(OpenAiRequest body);
}

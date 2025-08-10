package com.examsolver.openai.service.client;

import com.examsolver.openai.dto.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
@Primary
@Profile("stub")
@Slf4j
public class OpenAiClientStub implements OpenAiClientPort {

    @Override
    public Mono<OpenAiResponse> chatCompletions(OpenAiRequest body) {
        log.info("[STUB] Simulating OpenAI API call for model: {}", body.getModel());
        return Mono.just(
                OpenAiResponse.builder()
                        .id("stub-response")
                        .choices(List.of(
                                Choice.builder()
                                        .message(new Message(Role.ASSISTANT, "stub: ok"))
                                        .build()
                        ))
                        .usage(OpenAiResponse.Usage.builder()
                                .prompt_tokens(10)
                                .completion_tokens(20)
                                .total_tokens(30)
                                .build())
                        .build()
        );
    }
}

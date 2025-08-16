package com.examsolver.openai.service.client;

import com.examsolver.openai.dto.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Primary
@Profile("stub")
@RequiredArgsConstructor
@Slf4j
public class OpenAiClientStub implements OpenAiClientPort {

    @Value("${openai.stub.latency:200ms}")
    private Duration latency;

    @Value("${openai.stub.error-rate:0.0}") // 0.0–1.0
    private double errorRate;

    @Override
    public Mono<OpenAiResponse> chatCompletions(final OpenAiRequest body) {
        final long t0 = System.nanoTime();

        return Mono.defer(() -> {
                    // Posible fallo simulado
                    if (ThreadLocalRandom.current().nextDouble() < errorRate) {
                        // alterna entre 429 y 500 simulados
                        final int code = ThreadLocalRandom.current().nextBoolean() ? 429 : 500;
                        final RuntimeException ex = new RuntimeException("STUB_HTTP_" + code);
                        log.warn("[STUB] Simulating error code={} for model={}", code, body.getModel());
                        return Mono.<OpenAiResponse>error(ex);
                    }

                    // “Tokenización” simple: ~4 chars/token
                    final String prompt = body.getMessages() != null && !body.getMessages().isEmpty()
                            ? safe(body.getMessages().get(0).getContent())
                            : "";
                    final int promptTokens = Math.max(1, prompt.length() / 4);
                    final int completionTokens = 20; // fijo o hazlo configurable si quieres
                    final int totalTokens = promptTokens + completionTokens;

                    final OpenAiResponse resp = OpenAiResponse.builder()
                            .id("stub-" + System.currentTimeMillis())
                            .choices(List.of(
                                    Choice.builder()
                                            .message(new Message(Role.ASSISTANT, "stub: ok"))
                                            .finishReason("stop")
                                            .build()
                            ))
                            .usage(OpenAiResponse.Usage.builder()
                                    .prompt_tokens(promptTokens)
                                    .completion_tokens(completionTokens)
                                    .total_tokens(totalTokens)
                                    .build())
                            .build();

                    return Mono.just(resp);
                })
                .delayElement(latency)
                .doOnSuccess(r -> {
                    final long ms = (System.nanoTime() - t0) / 1_000_000;
                    log.info("[STUB] openai.chat.ok model={} latencyMs={} tokens[p/c/t]={}/{}/{}",
                            body.getModel(), ms,
                            r.getUsage() != null ? r.getUsage().getPrompt_tokens() : null,
                            r.getUsage() != null ? r.getUsage().getCompletion_tokens() : null,
                            r.getUsage() != null ? r.getUsage().getTotal_tokens() : null);
                })
                .doOnError(e -> {
                    final long ms = (System.nanoTime() - t0) / 1_000_000;
                    log.warn("[STUB] openai.chat.error model={} latencyMs={} err={}",
                            body.getModel(), ms, e.toString());
                });
    }

    private static String safe(final String s) {
        return s == null ? "" : s;
    }
}

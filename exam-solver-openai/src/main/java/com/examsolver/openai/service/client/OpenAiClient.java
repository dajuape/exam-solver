package com.examsolver.openai.service.client;

import com.examsolver.openai.config.OpenAiProperties;
import com.examsolver.openai.dto.client.OpenAiRequest;
import com.examsolver.openai.dto.client.OpenAiResponse;
import com.examsolver.openai.exception.OpenAiUpstreamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Profile("!stub")
@Slf4j
public class OpenAiClient implements OpenAiClientPort {

    private final WebClient webClient;
    private final OpenAiProperties p;

    @Override
    public Mono<OpenAiResponse> chatCompletions(OpenAiRequest body) {
        return webClient.post()
                .uri("")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> resp.createException().flatMap(Mono::error))
                .bodyToMono(OpenAiResponse.class)
                .timeout(p.getTimeout().getText())
                .retryWhen(retrySpec())
                .onErrorMap(WebClientResponseException.class,
                        e -> new OpenAiUpstreamException("OPENAI_" + e.getStatusCode().value(), e));
    }

    private Retry retrySpec() {
        return Retry.backoff(p.getMaxRetries(), p.getRetry().getInitialDelay())
                .maxBackoff(p.getRetry().getMaxDelay())
                .jitter(0.3)
                .filter(this::isRetryable);
    }

    private boolean isRetryable(Throwable t) {
        if (t instanceof WebClientResponseException w) {
            int code = w.getStatusCode().value();
            return code == 429 || w.getStatusCode().is5xxServerError();
        }
        return false;
    }
}

package com.examsolver.openai.service.client;

import com.examsolver.openai.config.OpenAiProperties;
import com.examsolver.openai.dto.client.OpenAiRequest;
import com.examsolver.openai.dto.client.OpenAiResponse;
import com.examsolver.openai.exception.OpenAiUpstreamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
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
    public Mono<OpenAiResponse> chatCompletions(final OpenAiRequest body) {
        final long t0 = System.nanoTime();

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .exchangeToMono(resp -> {
                    if (resp.statusCode().isError()) {
                        return resp.createException().flatMap(Mono::error);
                    }
                    return resp.toEntity(OpenAiResponse.class);
                })
                .timeout(p.getTimeout().getText())
                .retryWhen(retrySpec())
                .map(entity -> {
                    final String reqId = entity.getHeaders().getFirst("x-request-id");
                    log.info("openai.chat.ok requestId={} status={}", reqId, entity.getStatusCode().value());
                    return entity.getBody();
                })
                .onErrorMap(this::mapToUpstreamException);

    }

    private Retry retrySpec() {
        return Retry.backoff(p.getMaxRetries(), p.getRetry().getInitialDelay())
                .maxBackoff(p.getRetry().getMaxDelay())
                .jitter(0.3)
                .filter(this::isRetryable)
                .doBeforeRetry(rs -> {
                    final Throwable t = rs.failure();
                    final String reason = (t instanceof WebClientResponseException w)
                            ? "HTTP " + w.getStatusCode().value()
                            : t.getClass().getSimpleName();
                    log.warn("openai.chat.retry attempt={} dueTo={}", rs.totalRetries() + 1, reason);
                });
    }

    private boolean isRetryable(final Throwable t) {
        if (t instanceof WebClientResponseException w) {
            final int code = w.getStatusCode().value();
            return code == 429 || w.getStatusCode().is5xxServerError();
        }

        return t instanceof java.util.concurrent.TimeoutException
                || t instanceof java.net.ConnectException
                || t instanceof java.net.SocketTimeoutException;
    }

    private RuntimeException mapToUpstreamException(final Throwable t) {
        if (t instanceof WebClientResponseException w) {
            return new OpenAiUpstreamException("OPENAI_" + w.getStatusCode().value(), w);
        }
        if (t instanceof java.util.concurrent.TimeoutException) {
            return new OpenAiUpstreamException("OPENAI_TIMEOUT", t);
        }
        if (t instanceof java.net.ConnectException || t instanceof java.net.SocketTimeoutException) {
            return new OpenAiUpstreamException("OPENAI_IO", t);
        }
        return new OpenAiUpstreamException("OPENAI_UNKNOWN", t);
    }
}

package com.examsolver.openai.service.processing.strategy;

import com.examsolver.openai.config.ModelSelector;
import com.examsolver.openai.config.OpenAiProperties;
import com.examsolver.openai.dto.client.*;
import com.examsolver.openai.dto.client.OpenAiResponse.Usage;
import com.examsolver.openai.service.client.OpenAiClientPort;
import com.examsolver.openai.service.prompt.PromptBuilderService;
import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO;
import com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
@Slf4j
public class TextProcessingStrategy implements ProcessingStrategy {

    private final OpenAiClientPort client;
    private final OpenAiProperties props;
    private final ModelSelector modelSelector;
    private final PromptBuilderService promptBuilder;

    @Override
    public OpenAiProcessResponseDTO process(final OpenAIRequestDTO request) {
        final String model = modelSelector.text();
        final List<Indexed<String>> indexed = indexExercises(request);

        final AtomicInteger promptSum = new AtomicInteger(0);
        final AtomicInteger completionSum = new AtomicInteger(0);
        final AtomicInteger totalSum = new AtomicInteger(0);

        final List<Result> orderedResults = Flux.fromIterable(indexed)
                .flatMap(ix -> processExercise(request, model, ix), props.getMaxConcurrent())
                .doOnNext(ix -> accumulate(ix.value().usage(), promptSum, completionSum, totalSum))
                .map(ix -> new Indexed<>(ix.index(), ix.value().result()))
                .collectList()
                .map(this::orderAndUnwrap)
                .block();

        return OpenAiProcessResponseDTO.builder()
                .examId(UUID.fromString(request.getExamId()))
                .modelUsed(model)
                .results(orderedResults)
                .promptTokens(zeroToNull(promptSum.get()))
                .completionTokens(zeroToNull(completionSum.get()))
                .totalTokens(zeroToNull(totalSum.get()))
                .build();
    }

    /**
     * Assigns each exercise a stable index for later ordered aggregation.
     */
    private List<Indexed<String>> indexExercises(final OpenAIRequestDTO request) {
        return IntStream.range(0, request.getExercises().size())
                .mapToObj(i -> new Indexed<>(i, request.getExercises().get(i)))
                .toList();
    }

    /**
     * Processes one exercise: build prompt, call OpenAI, return {Result, Usage} with index.
     */
    private Mono<Indexed<ResultWithUsage>> processExercise(final OpenAIRequestDTO req,
                                                           final String model,
                                                           final Indexed<String> ix) {

        final String prompt = promptBuilder.buildText(req.getMode(), req.getDetectedLanguage(), ix.value());
        final OpenAiRequest body = buildBody(model, prompt);
        final long t0 = System.nanoTime();

        return client.chatCompletions(body)
                .map(resp -> new Indexed<>(ix.index(), new ResultWithUsage(
                        toResult(ix.index(), resp),
                        firstUsage(resp)
                )))
                .doOnNext(r -> {
                    final long ms = (System.nanoTime() - t0) / 1_000_000;
                    log.info("openai.text.done exerciseIndex={} latencyMs={}", r.index(), ms);
                });
    }

    /**
     * Creates the OpenAI request payload with the model and prompt.
     */
    private OpenAiRequest buildBody(final String model, final String prompt) {
        return OpenAiRequest.builder()
                .model(model)
                .messages(List.of(new Message(Role.USER, prompt)))
                .build();
    }

    /**
     * Restores original order and unwraps the Result list.
     */
    private List<Result> orderAndUnwrap(final List<Indexed<Result>> list) {
        list.sort(Comparator.comparingInt(Indexed::index));
        return list.stream().map(Indexed::value).toList();
    }

    /**
     * Maps OpenAI response to a single exercise Result.
     */
    private Result toResult(final int idx, final OpenAiResponse resp) {
        final Choice c = firstChoice(resp);
        final String content = c != null && c.getMessage() != null ? safe(c.getMessage().getContent()) : "";
        return Result.builder()
                .index(idx)
                .content(content)
                .finishReason(null)
                .build();
    }

    /**
     * Accumulates usage tokens (null-safe).
     */
    private void accumulate(final Usage u,
                            final AtomicInteger promptSum,
                            final AtomicInteger completionSum,
                            final AtomicInteger totalSum) {
        if (u == null) return;
        if (u.getPrompt_tokens() != null) promptSum.addAndGet(u.getPrompt_tokens());
        if (u.getCompletion_tokens() != null) completionSum.addAndGet(u.getCompletion_tokens());
        if (u.getTotal_tokens() != null) totalSum.addAndGet(u.getTotal_tokens());
    }

    /**
     * Returns null for zero to keep the response compact.
     */
    private Integer zeroToNull(final int v) {
        return v == 0 ? null : v;
    }

    /**
     * Extracts the Usage block from the response.
     */
    private Usage firstUsage(final OpenAiResponse resp) {
        return resp != null ? resp.getUsage() : null;
    }

    /**
     * Retrieves the first choice from the response, if present.
     */
    private Choice firstChoice(final OpenAiResponse resp) {
        return (resp != null && resp.getChoices() != null && !resp.getChoices().isEmpty())
                ? resp.getChoices().get(0)
                : null;
    }

    /**
     * Ensures returned content is never null.
     */
    private String safe(final String s) {
        return s == null ? "" : s;
    }

    /**
     * Small record to pair an item with its original index.
     */
    private record Indexed<T>(int index, T value) {
    }

    /**
     * Composite carrier for an exercise result and its usage.
     */
    private record ResultWithUsage(Result result, Usage usage) {
    }
}

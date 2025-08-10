package com.examsolver.openai.service.processing.strategy;

import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Resolves the appropriate {@link ProcessingStrategy} based on the request.
 * <p>Maps Vision → {@link VisionProcessingStrategy}, Text → {@link TextProcessingStrategy}.</p>
 */
@Component
public class ProcessingStrategyResolver {

    private final Map<Boolean, ProcessingStrategy> strategies;

    public ProcessingStrategyResolver(final List<ProcessingStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(this::isVision, s -> s));
    }

    public ProcessingStrategy resolve(final OpenAIRequestDTO req) {
        final boolean vision = Boolean.TRUE.equals(req.getFallbackRequired());
        return strategies.get(vision);
    }

    private boolean isVision(final ProcessingStrategy s) {
        return s instanceof VisionProcessingStrategy;
    }
}

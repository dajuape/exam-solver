package com.examsolver.openai.service.processing.strategy;

import com.examsolver.openai.config.ModelSelector;
import com.examsolver.openai.config.OpenAiProperties;
import com.examsolver.openai.service.client.OpenAiClient;
import com.examsolver.openai.service.client.OpenAiClientPort;
import com.examsolver.openai.service.prompt.PromptBuilderService;
import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Strategy for processing exams using OpenAI's vision capabilities.
 * <p>
 * This strategy is used when {@code fallbackRequired = true},
 * typically making a single request with a base64-encoded image.
 */
@Service
@RequiredArgsConstructor
public class VisionProcessingStrategy implements ProcessingStrategy {

    private final OpenAiClientPort client;
    private final OpenAiProperties props;
    private final ModelSelector modelSelector;
    private final PromptBuilderService promptBuilder;

    @Override
    public Mono<OpenAiProcessResponseDTO> process(final OpenAIRequestDTO request) {
        // Will be implemented in Step 2 (vision route)
        throw new UnsupportedOperationException("VisionProcessingStrategy not implemented yet");
    }
}

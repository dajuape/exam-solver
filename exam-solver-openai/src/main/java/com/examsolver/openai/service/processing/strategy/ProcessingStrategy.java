package com.examsolver.openai.service.processing.strategy;

import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO;
import reactor.core.publisher.Mono;


public interface ProcessingStrategy {

    /**
     * Processes the given exam request and returns the results.
     *
     * @param request the exam data and metadata
     * @return the processed response containing solutions or corrections
     */
    Mono<OpenAiProcessResponseDTO> process(OpenAIRequestDTO request);
}
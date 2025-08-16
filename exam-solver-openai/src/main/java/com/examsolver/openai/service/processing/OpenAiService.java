package com.examsolver.openai.service.processing;

import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO;
import reactor.core.publisher.Mono;

/**
 * Service for processing exams with OpenAI.
 * Delegates to the proper strategy (text/vision), applies concurrency/retries,
 * and returns results in original exercise order.
 */
public interface OpenAiService {

    /**
     * Processes the given exam request and returns ordered results.
     *
     * @param request preprocessed exam data and metadata
     * @return solutions/corrections in original order
     */
    Mono<OpenAiProcessResponseDTO> processExam(OpenAIRequestDTO request);
}

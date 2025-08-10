package com.examsolver.openai.service.processing.strategy;

import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO;


public interface ProcessingStrategy {

    /**
     * Processes the given exam request and returns the results.
     *
     * @param request the exam data and metadata
     * @return the processed response containing solutions or corrections
     */
    OpenAiProcessResponseDTO process(OpenAIRequestDTO request);
}
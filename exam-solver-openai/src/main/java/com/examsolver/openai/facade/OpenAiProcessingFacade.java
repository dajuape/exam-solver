package com.examsolver.openai.facade;


import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO;

import java.util.List;

/**
 * High-level facade to orchestrate validation, prompt building,
 * model selection and the downstream OpenAI call.
 */
public interface OpenAiProcessingFacade {

    /**
     * Validates and processes the incoming request exercise-by-exercise (sequential for now).
     *
     * @param request the OpenAI request payload containing mode, language, flags and exercises
     * @return a response per exercise, preserving the input order
     */
    List<OpenAiProcessResponseDTO> process(OpenAIRequestDTO request);
}

package com.examsolver.openai.service.validation;

import com.examsolver.shared.dtos.request.OpenAIRequestDTO;

/**
 * Validates that a given {@link OpenAIRequestDTO} contains
 * the minimum required data to start a call to the OpenAI API.
 */
public interface RequestValidatorService {

    /**
     * Validates the given request for OpenAI processing.
     *
     * @param request the request to validate
     * @throws IllegalArgumentException if the request is invalid
     */
    void validate(OpenAIRequestDTO request);
}

package com.examsolver.openai.exception;

/**
 * Exception thrown when an OpenAI request fails validation.
 */
public class OpenAiValidationException extends RuntimeException {

    public OpenAiValidationException(String message) {
        super(message);
    }
}

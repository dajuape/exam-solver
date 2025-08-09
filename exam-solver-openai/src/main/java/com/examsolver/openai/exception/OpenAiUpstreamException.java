package com.examsolver.openai.exception;

/**
 * Represents an upstream failure when communicating with the OpenAI API.
 */
public class OpenAiUpstreamException extends RuntimeException {

    public OpenAiUpstreamException(final String message) {
        super(message);
    }

    public OpenAiUpstreamException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

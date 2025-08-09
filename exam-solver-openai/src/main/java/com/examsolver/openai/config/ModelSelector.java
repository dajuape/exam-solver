package com.examsolver.openai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Utility to select the appropriate OpenAI model
 * based on usage context.
 */
@Component
@RequiredArgsConstructor
public class ModelSelector {

    private final OpenAiProperties props;

    /**
     * Returns the default text model (for standard text processing).
     */
    public String text() {
        return props.getModel().getText();
    }

    /**
     * Returns the vision model (for image-based processing).
     */
    public String vision() {
        return props.getModel().getVision();
    }

    /**
     * Returns the fallback model (for degraded or recovery scenarios).
     */
    public String fallback() {
        return props.getModel().getFallback();
    }
}

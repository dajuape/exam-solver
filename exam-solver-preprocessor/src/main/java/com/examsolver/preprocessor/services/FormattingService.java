package com.examsolver.preprocessor.services;

public interface FormattingService {

    /**
     * Formats the exercises with clear headers and separators.
     *
     * @param exercises List of exercise texts.
     * @return Formatted text.
     */
    String format(java.util.List<String> exercises);
}

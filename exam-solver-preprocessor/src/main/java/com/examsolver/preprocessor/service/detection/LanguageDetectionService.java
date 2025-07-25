package com.examsolver.preprocessor.service.detection;

public interface LanguageDetectionService {

    /**
     * Detects the language of a given text input and returns its ISO 639-1 code.
     *
     * <p>Examples include "es" for Spanish, "en" for English, "fr" for French, and "de" for German.</p>
     *
     * @param input the raw text to analyze
     * @return a lowercase ISO language code (e.g., "es"), or "unknown" if detection fails
     */
    String detectLanguage(String input);
}

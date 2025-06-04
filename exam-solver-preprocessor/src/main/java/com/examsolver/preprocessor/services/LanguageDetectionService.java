package com.examsolver.preprocessor.services;

public interface LanguageDetectionService {

    /**
     * Detects the language code (e.g., "es", "en", "fr", "de") for the input text.
     *
     * @param input Text to analyze.
     * @return ISO language code, or "unknown" if not detected.
     */
    String detectLanguage(String input);
}

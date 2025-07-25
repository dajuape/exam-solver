package com.examsolver.preprocessor.service.cleaner;

public interface TextCleaningService {

    /**
     * Cleans and normalizes the given text.
     *
     * @param input Raw text to clean.
     * @return Cleaned text.
     */
    String clean(String input);
}

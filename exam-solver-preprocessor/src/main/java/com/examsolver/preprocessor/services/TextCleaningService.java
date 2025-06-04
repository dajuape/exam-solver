package com.examsolver.preprocessor.services;

public interface TextCleaningService {

    /**
     * Cleans and normalizes the given text.
     *
     * @param input Raw text to clean.
     * @return Cleaned text.
     */
    String clean(String input);
}

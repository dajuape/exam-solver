package com.examsolver.preprocessor.service.splitting;

import java.util.List;

public interface ExerciseSplitterService {

    /**
     * Splits the input exam text into a list of exercises based on the specified delimiter.
     *
     * <p>This is typically used after cleaning and normalization, where a standard
     * marker like "=== EJERCICIO ===" has been inserted before each exercise heading.</p>
     *
     * @param input     the full preprocessed exam text
     * @param delimiter the delimiter string that marks the start of each exercise
     * @return a list of exercises as separate text blocks
     */
    List<String> split(String input, String delimiter);
}

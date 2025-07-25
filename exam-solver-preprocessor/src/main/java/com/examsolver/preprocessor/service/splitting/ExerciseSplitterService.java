package com.examsolver.preprocessor.service.splitting;

import java.util.List;

public interface ExerciseSplitterService {

    /**
     * Splits the input text into a list of exercises using the given delimiter.
     *
     * @param input     Exam text to split.
     * @param delimiter Delimiter that marks the start of each exercise.
     * @return List of exercises as strings.
     */
    List<String> split(String input, String delimiter);
}

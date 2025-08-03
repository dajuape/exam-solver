package com.examsolver.preprocessor.service.delimiter;

public interface ExerciseDelimiterService {

    /**
     * Inserts a delimiter line (e.g., "=== EXERCISE ===") before each exercise heading.
     *
     * <p>Matches variants like "Ejercicio A1", "Ejercicio B2", etc., in any casing
     * and with optional markdown formatting (asterisks).</p>
     *
     * @param text      input text
     * @param delimiter input delimiter
     * @return text with exercise delimiters inserted
     */
    String setDeilimter(String text, String delimiter);
}

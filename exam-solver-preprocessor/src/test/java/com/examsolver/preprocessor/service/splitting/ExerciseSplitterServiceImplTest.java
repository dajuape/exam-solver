package com.examsolver.preprocessor.service.splitting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExerciseSplitterServiceImplTest {

    private ExerciseSplitterServiceImpl splitterService;

    @BeforeEach
    void setUp() {
        splitterService = new ExerciseSplitterServiceImpl();
    }

    @Test
    void shouldSplitTextByDelimiter() {
        String input = "=== EXERCISE ===\nExercise 1 content\n=== EXERCISE ===\nExercise 2 content";
        String delimiter = "=== EXERCISE ===";

        List<String> result = splitterService.split(input, delimiter);

        assertEquals(2, result.size());
        assertEquals("Exercise 1 content", result.get(0));
        assertEquals("Exercise 2 content", result.get(1));
    }

    @Test
    void shouldTrimWhitespaceAroundSegments() {
        String input = "=== EXERCISE ===\n   Exercise A   \n=== EXERCISE ===\n  Exercise B ";
        String delimiter = "=== EXERCISE ===";

        List<String> result = splitterService.split(input, delimiter);

        assertEquals(2, result.size());
        assertEquals("Exercise A", result.get(0));
        assertEquals("Exercise B", result.get(1));
    }

    @Test
    void shouldIgnoreEmptySegments() {
        String input = "=== EXERCISE ===\nExercise 1\n=== EXERCISE ===\n\n=== EXERCISE ===\nExercise 2";
        String delimiter = "=== EXERCISE ===";

        List<String> result = splitterService.split(input, delimiter);

        assertEquals(2, result.size());
        assertEquals("Exercise 1", result.get(0));
        assertEquals("Exercise 2", result.get(1));
    }

    @Test
    void shouldReturnEmptyListWhenInputIsNull() {
        List<String> result = splitterService.split(null, "=== EXERCISE ===");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenDelimiterIsNull() {
        List<String> result = splitterService.split("some content", null);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenDelimiterIsEmpty() {
        List<String> result = splitterService.split("some content", "");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenInputIsEmpty() {
        List<String> result = splitterService.split("", "=== EXERCISE ===");
        assertTrue(result.isEmpty());
    }
}

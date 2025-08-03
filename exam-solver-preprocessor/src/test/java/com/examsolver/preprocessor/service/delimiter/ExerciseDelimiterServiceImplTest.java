package com.examsolver.preprocessor.service.delimiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link ExerciseDelimiterServiceImpl} supporting multiple languages.
 */
class ExerciseDelimiterServiceImplTest {

    private ExerciseDelimiterService service;

    @BeforeEach
    void setUp() {
        service = new ExerciseDelimiterServiceImpl();
    }

    @Test
    void testSetDelimiter_SpanishEjercicio() {
        String input = "Introducción\nEjercicio A1\nContenido";
        String delimiter = "=== EJERCICIO ===";
        String expected = "Introducción\n=== EJERCICIO ===\nEjercicio A1\nContenido";

        String result = service.setDeilimter(input, delimiter);
        assertEquals(expected, result);
    }

    @Test
    void testSetDelimiter_EnglishExercise() {
        String input = "Intro\nExercise B2\nDetails";
        String delimiter = "=== EXERCISE ===";
        String expected = "Intro\n=== EXERCISE ===\nExercise B2\nDetails";

        String result = service.setDeilimter(input, delimiter);
        assertEquals(expected, result);
    }

    @Test
    void testSetDelimiter_FrenchExercice() {
        String input = "Début\nExercice C3\nTexte";
        String delimiter = "=== EXERCICE ===";
        String expected = "Début\n=== EXERCICE ===\nExercice C3\nTexte";

        String result = service.setDeilimter(input, delimiter);
        assertEquals(expected, result);
    }

    @Test
    void testSetDelimiter_GermanUebung() {
        String input = "Einleitung\nÜbung D4\nBeschreibung";
        String delimiter = "=== AUFGABE ===";
        String expected = "Einleitung\n=== AUFGABE ===\nÜbung D4\nBeschreibung";

        String result = service.setDeilimter(input, delimiter);
        assertEquals(expected, result);
    }

    @Test
    void testSetDelimiter_GermanAufgabe() {
        String input = "Start\nAufgabe E5\nErklärung";
        String delimiter = "=== AUFGABE ===";
        String expected = "Start\n=== AUFGABE ===\nAufgabe E5\nErklärung";

        String result = service.setDeilimter(input, delimiter);
        assertEquals(expected, result);
    }

    @Test
    void testSetDelimiter_MultipleLanguages() {
        String input = """
                Intro
                Ejercicio A1
                Text
                Exercise B2
                Description
                Exercice C3
                Texte
                Übung D4
                Erklärung
                Aufgabe E5
                Weiter
                """;
        String delimiter = "=== DELIMITER ===";
        String expected = """
                Intro
                === DELIMITER ===
                Ejercicio A1
                Text
                === DELIMITER ===
                Exercise B2
                Description
                === DELIMITER ===
                Exercice C3
                Texte
                === DELIMITER ===
                Übung D4
                Erklärung
                === DELIMITER ===
                Aufgabe E5
                Weiter
                """;

        String result = service.setDeilimter(input, delimiter);
        assertEquals(expected, result);
    }

    @Test
    void testSetDelimiter_NoMatches() {
        String input = "No exercises here.";
        String delimiter = "=== ANY ===";

        String result = service.setDeilimter(input, delimiter);
        assertEquals(input, result);
    }

    @Test
    void testSetDelimiter_EmptyInput() {
        String result = service.setDeilimter("", "=== X ===");
        assertEquals("", result);
    }

    @Test
    void testSetDelimiter_NullInput_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.setDeilimter(null, "==="));
    }
}

package com.examsolver.preprocessor.service.detection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


class LanguageDetectionServiceImplTest {

    private LanguageDetectionServiceImpl service;

    @BeforeEach
    void setUp() throws IOException {
        service = new LanguageDetectionServiceImpl();
    }

    @Test
    void shouldDetectEnglishLanguage() {
        String input = "This is a test sentence written in English.";
        String detected = service.detectLanguage(input);
        assertEquals("en", detected);
    }

    @Test
    void shouldDetectSpanishLanguage() {
        String input = "Este es un texto de prueba en español.";
        String detected = service.detectLanguage(input);
        assertEquals("es", detected);
    }

    @Test
    void shouldDetectFrenchLanguage() {
        String input = "Ceci est une phrase de test en français.";
        String detected = service.detectLanguage(input);
        assertEquals("fr", detected);
    }

    @Test
    void shouldDetectGermanLanguage() {
        String input = "Dies ist ein Testsatz auf Deutsch.";
        String detected = service.detectLanguage(input);
        assertEquals("de", detected);
    }

    @Test
    void shouldReturnUnknownForNullInput() {
        String detected = service.detectLanguage(null);
        assertEquals("unknown", detected);
    }

    @Test
    void shouldReturnUnknownForEmptyInput() {
        String detected = service.detectLanguage("");
        assertEquals("unknown", detected);
    }

    @Test
    void shouldReturnUnknownForNonLinguisticInput() {
        String detected = service.detectLanguage("12345 ### ---");
        assertEquals("unknown", detected);
    }
}

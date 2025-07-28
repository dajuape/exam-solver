package com.examsolver.preprocessor.service.cleaner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TextCleaningServiceImplTest {

    private TextCleaningServiceImpl cleaningService;

    @BeforeEach
    void setUp() {
        cleaningService = new TextCleaningServiceImpl();
        ReflectionTestUtils.setField(cleaningService, "maxLineLength", 50);
    }

    @Test
    void shouldRemoveTabsAndExtraSpaces() {
        String input = "\tThis\tis\t  a\ttest   ";
        String result = cleaningService.clean(input);
        assertEquals("This is a test", result);
    }

    @Test
    void shouldFixHyphenatedLineBreaks() {
        String input = "equ-\nation complete";
        String result = cleaningService.clean(input);
        assertEquals("equation complete", result);
    }

    @Test
    void shouldJoinLinesWithLowercaseStart() {
        String input = "This is a line\nfollowed by lowercase";
        String result = cleaningService.clean(input);
        assertEquals("This is a line followed by lowercase", result);
    }

    @Test
    void shouldRemovePageNumbers() {
        String input = "123\nContent\n2\nOther content";
        String result = cleaningService.clean(input);
        assertEquals("Content\nOther content", result);
    }

    @Test
    void shouldRemoveRepeatedShortLines() {
        String input = "Header\nLine one\nHeader\nLine two";
        String result = cleaningService.clean(input);
        assertEquals("Line one\nLine two", result);
    }

    @Test
    void shouldNormalizeNewlinesAndSpaces() {
        String input = "Text   with    spaces\n\n\nmore lines";
        String result = cleaningService.clean(input);
        assertEquals("Text with spaces\nmore lines", result);
    }

    @Test
    void shouldTrimEachLine() {
        String input = "   Trim this line   \n  And this one   ";
        String result = cleaningService.clean(input);
        assertEquals("Trim this line\nAnd this one", result);
    }

    @Test
    void shouldInsertEjercicioMarkers() {
        String input = "Ejercicio A1\nThis is an Ejercicio.\n*Ejercicio B2*\nAnother Ejercicio.";
        String result = cleaningService.clean(input);
        assertTrue(result.contains("=== EJERCICIO ===\nEjercicio A1"));
        assertTrue(result.contains("=== EJERCICIO ===\n*Ejercicio B2*"));
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(cleaningService.clean(null));
    }

    @Test
    void shouldProcessComplexInputCorrectly() {
        String input = """
                Page 1
                Ejercicio A1
                Let x be a variable.
                
                Page 1
                *Ejercicio B2*
                Compute the integral.
                """;

        String result = cleaningService.clean(input);

        assertFalse(result.contains("Page 1")); // repeated header removed
        assertTrue(result.contains("=== EJERCICIO ===\nEjercicio A1"));
        assertTrue(result.contains("=== EJERCICIO ===\n*Ejercicio B2*"));
        assertFalse(result.contains("  ")); // no multiple spaces
    }
}

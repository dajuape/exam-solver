package com.examsolver.preprocessor.service.detection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScientificDetectorServiceImplTest {

    private ScientificDetectorServiceImpl detector;

    @BeforeEach
    void setUp() {
        detector = new ScientificDetectorServiceImpl();
        // Set minMatches via reflection as it's injected from @Value in real runtime
        ReflectionTestUtils.setField(detector, "minMatches", 2);
    }

    @Test
    void shouldReturnTrueWhenTextContainsEnoughScientificPatterns() {
        String input = "f(x) = 3x + 5 and \\frac{a}{b} is used often";
        assertTrue(detector.isScientific(input));
    }

    @Test
    void shouldReturnFalseWhenTextContainsOnlyOneMatch() {
        String input = "This is a math function: f(x)";
        assertFalse(detector.isScientific(input));
    }

    @Test
    void shouldReturnFalseWhenTextContainsNoScientificPatterns() {
        String input = "This is a regular paragraph about literature and language.";
        assertFalse(detector.isScientific(input));
    }

    @Test
    void shouldReturnTrueWithLaTeXAndMathSymbols() {
        String input = "\\begin{equation} x = 5 \\end{equation} and ∫ x dx";
        assertTrue(detector.isScientific(input));
    }

    @Test
    void shouldReturnTrueWhenTextContainsMultipleSimpleMatches() {
        String input = "3x and x = 7 are both algebraic.";
        assertTrue(detector.isScientific(input));
    }

    @Test
    void shouldHandleEmptyInput() {
        String input = "";
        assertFalse(detector.isScientific(input));
    }

    @Test
    void shouldHandleNullInput() {
        String input = null;
        assertFalse(detector.isScientific(input));
    }
}

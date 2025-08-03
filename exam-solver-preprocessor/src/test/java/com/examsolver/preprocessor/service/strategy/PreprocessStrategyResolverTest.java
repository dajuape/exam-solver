package com.examsolver.preprocessor.service.strategy;

import com.examsolver.shared.enums.FileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PreprocessStrategyResolver}
 */
class PreprocessStrategyResolverTest {

    private PdfPreprocessServiceImpl pdfStrategy;
    private OcrPreprocessServiceImpl ocrStrategy;
    private PreprocessStrategyResolver resolver;

    @BeforeEach
    void setUp() {
        // Create mocks for each strategy
        pdfStrategy = mock(PdfPreprocessServiceImpl.class);
        ocrStrategy = mock(OcrPreprocessServiceImpl.class);

        // Initialize resolver with both strategies
        resolver = new PreprocessStrategyResolver(List.of(pdfStrategy, ocrStrategy));
    }

    @Test
    void testResolvePdfStrategy() {
        // Ensure PDF strategy is resolved correctly
        PreprocessStrategy resolved = resolver.resolve(FileType.PDF);
        assertSame(pdfStrategy, resolved);
    }

    @Test
    void testResolveOcrStrategy() {
        // Ensure IMAGE strategy is resolved correctly
        PreprocessStrategy resolved = resolver.resolve(FileType.IMAGE);
        assertSame(ocrStrategy, resolved);
    }

    @Test
    void testResolveUnknownStrategy_ThrowsException() {
        // Create a mock strategy of unknown type
        PreprocessStrategy unknownStrategy = mock(PreprocessStrategy.class);

        // Expect exception when trying to create resolver with unknown strategy
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new PreprocessStrategyResolver(List.of(unknownStrategy))
        );

        assertTrue(exception.getMessage().contains("Unknown strategy"));
    }

    @Test
    void testResolveWithNull_ReturnsNull() {
        // Should return null when no matching strategy is found
        PreprocessStrategy resolved = resolver.resolve(null);
        assertNull(resolved);
    }
}

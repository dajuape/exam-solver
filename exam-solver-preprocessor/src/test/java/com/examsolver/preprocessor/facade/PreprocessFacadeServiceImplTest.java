package com.examsolver.preprocessor.facade;

import com.examsolver.preprocessor.config.PreprocessorProperties;
import com.examsolver.preprocessor.service.analysis.PdfContentAnalyzerService;
import com.examsolver.preprocessor.service.cleaner.TextCleaningService;
import com.examsolver.preprocessor.service.detection.LanguageDetectionService;
import com.examsolver.preprocessor.service.detection.NoiseDetectionService;
import com.examsolver.preprocessor.service.fallback.FallbackOrchestrationHandler;
import com.examsolver.preprocessor.service.fallback.FallbackResult;
import com.examsolver.preprocessor.service.splitting.ExerciseSplitterService;
import com.examsolver.preprocessor.service.strategy.PreprocessStrategy;
import com.examsolver.preprocessor.service.strategy.PreprocessStrategyResolver;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.dtos.response.PreprocessResponseDTO;
import com.examsolver.shared.enums.FallbackReasonCode;
import com.examsolver.shared.enums.FileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreprocessFacadeServiceImplTest {

    private PreprocessStrategyResolver strategyResolver;
    private TextCleaningService cleaningService;
    private LanguageDetectionService languageDetectionService;
    private PreprocessorProperties properties;
    private ExerciseSplitterService splitterService;
    private FallbackOrchestrationHandler fallbackHandler;
    private PdfContentAnalyzerService pdfAnalyzer;
    private NoiseDetectionService noiseDetectionService;

    private PreprocessFacadeServiceImpl facade;

    @BeforeEach
    void setUp() {
        strategyResolver = mock(PreprocessStrategyResolver.class);
        cleaningService = mock(TextCleaningService.class);
        languageDetectionService = mock(LanguageDetectionService.class);
        properties = new PreprocessorProperties();
        splitterService = mock(ExerciseSplitterService.class);
        fallbackHandler = mock(FallbackOrchestrationHandler.class);
        pdfAnalyzer = mock(PdfContentAnalyzerService.class);
        noiseDetectionService = mock(NoiseDetectionService.class);

        properties.setExerciseDelimiters(Map.of("es", "=== EJERCICIO ===", "en", "=== EXERCISE ==="));

        facade = new PreprocessFacadeServiceImpl(
                strategyResolver,
                cleaningService,
                languageDetectionService,
                properties,
                splitterService,
                fallbackHandler,
                pdfAnalyzer,
                noiseDetectionService
        );
    }

    @Test
    void shouldUseOcrStrategyIfPdfIsScanned() throws Exception {
        byte[] fileBytes = "dummy".getBytes();
        String base64 = Base64.getEncoder().encodeToString(fileBytes);

        PreprocessRequestDTO dto = PreprocessRequestDTO.builder()
                .base64File(base64)
                .fileType(FileType.PDF)
                .fileName("exam.pdf")
                .build();

        when(pdfAnalyzer.isScanned(fileBytes)).thenReturn(true);

        PreprocessStrategy ocrStrategy = mock(PreprocessStrategy.class);
        when(strategyResolver.resolve(FileType.IMAGE)).thenReturn(ocrStrategy);

        when(fallbackHandler.extractWithFallback(dto, ocrStrategy))
                .thenReturn(FallbackResult.success("Ejercicio A1\ncontenido"));

        when(cleaningService.clean(any())).thenReturn("=== EJERCICIO ===\nEjercicio A1\ncontenido");
        when(languageDetectionService.detectLanguage(any())).thenReturn("es");
        when(noiseDetectionService.isTooNoisy(any())).thenReturn(false);
        when(splitterService.split(any(), eq("=== EJERCICIO ===")))
                .thenReturn(List.of("Ejercicio A1\ncontenido"));

        PreprocessResponseDTO response = facade.process(dto);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getExtractedText().size());
        assertFalse(response.isFallbackRequired());
    }

    @Test
    void shouldReturnEarlyIfNougatFails() throws Exception {
        byte[] fileBytes = "dummy".getBytes();
        String base64 = Base64.getEncoder().encodeToString(fileBytes);

        PreprocessRequestDTO dto = PreprocessRequestDTO.builder()
                .base64File(base64)
                .fileType(FileType.PDF)
                .fileName("exam.pdf")
                .build();

        when(pdfAnalyzer.isScanned(fileBytes)).thenReturn(false);

        PreprocessStrategy strategy = mock(PreprocessStrategy.class);
        when(strategyResolver.resolve(FileType.PDF)).thenReturn(strategy);

        FallbackResult fail = FallbackResult.failed("x = 3", FallbackReasonCode.NOUGAT_UNAVAILABLE);
        when(fallbackHandler.extractWithFallback(dto, strategy)).thenReturn(fail);

        PreprocessResponseDTO response = facade.process(dto);

        assertTrue(response.isSuccess());
        assertNull(response.getExtractedText());
        assertTrue(response.isFallbackRequired());
        assertEquals(FallbackReasonCode.NOUGAT_UNAVAILABLE, response.getFallbackCode());
    }

    @Test
    void shouldUseEnglishDelimiterIfLanguageDetectedIsEn() throws Exception {
        byte[] fileBytes = "dummy".getBytes();
        String base64 = Base64.getEncoder().encodeToString(fileBytes);

        PreprocessRequestDTO dto = PreprocessRequestDTO.builder()
                .base64File(base64)
                .fileType(FileType.PDF)
                .fileName("exam.pdf")
                .build();

        when(pdfAnalyzer.isScanned(fileBytes)).thenReturn(false);

        PreprocessStrategy strategy = mock(PreprocessStrategy.class);
        when(strategyResolver.resolve(FileType.PDF)).thenReturn(strategy);

        when(fallbackHandler.extractWithFallback(eq(dto), any()))
                .thenReturn(FallbackResult.success("Exercise A1\ntext"));

        when(cleaningService.clean(any())).thenReturn("=== EXERCISE ===\nExercise A1\ntext");
        when(languageDetectionService.detectLanguage(any())).thenReturn("en");
        when(noiseDetectionService.isTooNoisy(any())).thenReturn(false);
        when(splitterService.split(any(), eq("=== EXERCISE ===")))
                .thenReturn(List.of("Exercise A1\ntext"));

        PreprocessResponseDTO response = facade.process(dto);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getExtractedText().size());
    }

    @Test
    void shouldMarkUserConfirmationRequiredIfTextEmpty() throws Exception {
        byte[] fileBytes = "dummy".getBytes();
        String base64 = Base64.getEncoder().encodeToString(fileBytes);

        PreprocessRequestDTO dto = PreprocessRequestDTO.builder()
                .base64File(base64)
                .fileType(FileType.IMAGE)
                .fileName("scan.jpg")
                .build();

        when(pdfAnalyzer.isScanned(fileBytes)).thenReturn(false);

        PreprocessStrategy strategy = mock(PreprocessStrategy.class);
        when(strategyResolver.resolve(FileType.IMAGE)).thenReturn(strategy);

        FallbackResult fallback = FallbackResult.failed(null, FallbackReasonCode.EMPTY_EXTRACTION_RESULT);
        when(fallbackHandler.extractWithFallback(dto, strategy)).thenReturn(fallback);

        PreprocessResponseDTO response = facade.process(dto);

        assertTrue(response.isSuccess());
        assertTrue(response.isFallbackRequired());
        assertEquals(FallbackReasonCode.EMPTY_EXTRACTION_RESULT, response.getFallbackCode());
        assertTrue(response.isUserConfirmationRequired());
    }
}

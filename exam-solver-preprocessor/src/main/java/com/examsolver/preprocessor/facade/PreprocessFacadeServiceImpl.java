package com.examsolver.preprocessor.facade;

import com.examsolver.preprocessor.config.PreprocessorProperties;
import com.examsolver.preprocessor.service.analysis.PdfContentAnalyzerService;
import com.examsolver.preprocessor.service.cleaner.TextCleaningService;
import com.examsolver.preprocessor.service.delimiter.ExerciseDelimiterService;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PreprocessFacadeServiceImpl implements PreprocessFacadeService {

    private final static String DEFAUTL_DELIMITER = "=== EJERCICIO ===";

    private final PreprocessStrategyResolver strategyResolver;
    private final TextCleaningService textCleaningService;
    private final LanguageDetectionService languageDetectionService;
    private final PreprocessorProperties preprocessorProperties;
    private final ExerciseDelimiterService exerciseDelimiterService;
    private final ExerciseSplitterService exerciseSplitterService;
    private final FallbackOrchestrationHandler fallbackOrchestrationHandler;
    private final PdfContentAnalyzerService pdfContentAnalyzerService;
    private final NoiseDetectionService noiseDetectionService;

    @Override
    public PreprocessResponseDTO process(PreprocessRequestDTO request) throws IOException {
        final byte[] fileBytes = request.getDecodedFileBytes();
        final FileType originalFileType = request.getFileType();
        FileType effectiveFileType = originalFileType;

        if (originalFileType == FileType.PDF && pdfContentAnalyzerService.isScanned(fileBytes)) {
            log.info("PDF detected as scanned. Switching to OCR strategy.");
            effectiveFileType = FileType.IMAGE;
        }

        // Resolve strategy based on effective file type (PDF or scanned image)
        final PreprocessStrategy strategy = strategyResolver.resolve(effectiveFileType);

        final FallbackResult result = fallbackOrchestrationHandler.extractWithFallback(request, strategy);

        // If Nougat fallback failed, skip post-processing and return early
        if (result.nougatFailed()) {
            return PreprocessResponseDTO.builder()
                    .success(true)
                    .exercises(null)
                    .extractedText(null)
                    .detectedLanguage(null)
                    .fallbackRequired(true)
                    .fallbackCode(result.fallbackCode())
                    .userConfirmationRequired(result.fallbackCode() == FallbackReasonCode.EMPTY_EXTRACTION_RESULT)
                    .build();
        }

        // Clean and post-process
        final String cleaned = textCleaningService.clean(result.text());
        final String language = languageDetectionService.detectLanguage(cleaned);
        final String delimiter = preprocessorProperties.getExerciseDelimiters()
                .getOrDefault(language, DEFAUTL_DELIMITER);

        final String delimited = exerciseDelimiterService.setDeilimter(cleaned, delimiter);
        boolean ocrWasNoisy = noiseDetectionService.isTooNoisy(delimited);

        final List<String> exercises = exerciseSplitterService.split(cleaned, delimiter);

        final FallbackReasonCode code = ocrWasNoisy ? FallbackReasonCode.TEXT_TOO_NOISY : result.fallbackCode();
        return PreprocessResponseDTO.builder()
                .success(true)
                .exercises(exercises)
                .extractedText(cleaned)
                .detectedLanguage(language)
                .fallbackRequired(code != null)
                .fallbackCode(code)
                .userConfirmationRequired(code == FallbackReasonCode.EMPTY_EXTRACTION_RESULT)
                .build();

    }

}

package com.examsolver.preprocessor.services.impl;

import com.examsolver.preprocessor.config.PreprocessorProperties;
import com.examsolver.preprocessor.resolver.PreprocessStrategyResolver;
import com.examsolver.preprocessor.services.*;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.dtos.response.PreprocessResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PreprocessFacadeServiceImpl implements PreprocessFacadeService {

    private final PreprocessStrategyResolver strategyResolver;
    private final TextCleaningService textCleaningService;
    private final LanguageDetectionService languageDetectionService;
    private final PreprocessorProperties preprocessorProperties;
    private final ExerciseSplitterService exerciseSplitterService;
    private final ScientificDetectorService scientificDetectorService;
    private final NougatClientService nougatClientService;

    @Override
    public PreprocessResponseDTO process(PreprocessRequestDTO request) {
        // 1. Select and execute strategy (PDF/OCR)
        final PreprocessStrategy strategy = strategyResolver.resolve(request.getFileType());
        String rawText = strategy.extractText(request);
        
        final String cleaned = textCleaningService.clean(getExtractedText(request, rawText));

        final String language = languageDetectionService.detectLanguage(cleaned);

        final String delimiter = preprocessorProperties.getExerciseDelimiters().getOrDefault(language, "=== EJERCICIO ===");

        final List<String> exercises = exerciseSplitterService.split(cleaned, delimiter);

        return PreprocessResponseDTO.builder()
                .success(true)
                .extractedText(exercises)
                .fallbackRequired(false)
                .build();
    }

    private String getExtractedText(PreprocessRequestDTO request, String rawText) {
        if (scientificDetectorService.isScientific(rawText)) {
            log.info("Document appears to be scientific. Using Nougat OCR for reprocessing.");
            try {
                return nougatClientService.convertPdfToLatex(request.getDecodedFileBytes(), request.getFileName());

            } catch (Exception e) {
                log.warn("Nougat OCR fallback failed, keeping original text. Reason: {}", e.getMessage());
                throw new RuntimeException();
            }
        }
        return rawText;
    }

}

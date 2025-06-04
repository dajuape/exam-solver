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

    @Override
    public PreprocessResponseDTO process(PreprocessRequestDTO request) {
        // 1. Select and execute strategy (PDF/OCR)
        final PreprocessStrategy strategy = strategyResolver.resolve(request.getFileType());
        final PreprocessResponseDTO response = strategy.preprocess(request);

        if (response.isFallbackRequired()) {
            log.debug("Fallback is required in preprocessing due to: {}", response.getFallbackReason());
            return response;
        }

        final String cleaned = textCleaningService.clean(response.getExtractedText());
        response.setExtractedText(cleaned);

        final String language = languageDetectionService.detectLanguage(cleaned);

        final String delimiter = preprocessorProperties.getExerciseDelimiters().getOrDefault(language, "=== EJERCICIO ===");

        final List<String> exercises = exerciseSplitterService.split(cleaned, delimiter);

        return response;
    }

}

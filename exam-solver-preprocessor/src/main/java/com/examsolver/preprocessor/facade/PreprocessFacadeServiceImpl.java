package com.examsolver.preprocessor.facade;

import com.examsolver.preprocessor.config.PreprocessorProperties;
import com.examsolver.preprocessor.service.analysis.PdfContentAnalyzerService;
import com.examsolver.preprocessor.service.cleaner.TextCleaningService;
import com.examsolver.preprocessor.service.detection.LanguageDetectionService;
import com.examsolver.preprocessor.service.detection.NoiseDetectionService;
import com.examsolver.preprocessor.service.detection.ScientificDetectorService;
import com.examsolver.preprocessor.service.fallback.FallbackResult;
import com.examsolver.preprocessor.service.fallback.NougatClientService;
import com.examsolver.preprocessor.service.splitting.ExerciseSplitterService;
import com.examsolver.preprocessor.service.strategy.PreprocessStrategy;
import com.examsolver.preprocessor.service.strategy.PreprocessStrategyResolver;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.dtos.response.PreprocessResponseDTO;
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
    private final ExerciseSplitterService exerciseSplitterService;
    private final ScientificDetectorService scientificDetectorService;
    private final NougatClientService nougatClientService;
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

        // Execute initial OCR/text extraction
        String rawText = strategy.extractText(request);

        // Apply fallback if scientific content is detected (may switch to Nougat)
        FallbackResult result = applyScientificFallbackIfNeeded(request, rawText);

        // Clean and post-process
        String cleaned = textCleaningService.clean(result.text());
        String language = languageDetectionService.detectLanguage(cleaned);
        boolean ocrWasNoisy = noiseDetectionService.isTooNoisy(cleaned);

        final String delimiter = preprocessorProperties.getExerciseDelimiters()
                .getOrDefault(language, DEFAUTL_DELIMITER);
        final List<String> exercises = exerciseSplitterService.split(cleaned, delimiter);

        return PreprocessResponseDTO.builder()
                .success(true)
                .extractedText(exercises)
                .fallbackRequired(result.usedNougatFallback())
                .ocrWasNoisy(ocrWasNoisy)
                .detectedLanguage(language)
                .usedScientificFallback(result.usedNougatFallback())
                .build();
    }


    /**
     * Applies scientific fallback logic if the input text appears to contain scientific/mathematical content.
     * <p>
     * If such content is detected, it delegates to the Nougat service to reprocess the file into LaTeX format.
     * Otherwise, the original extracted text is used.
     *
     * @param request the original preprocessing request, used to re-extract bytes if needed
     * @param rawText the initially extracted text to analyze for scientific content
     * @return a {@link FallbackResult} containing the text to use (original or LaTeX) and a flag indicating if fallback was used
     * @throws RuntimeException if Nougat processing fails unexpectedly
     */
    private FallbackResult applyScientificFallbackIfNeeded(PreprocessRequestDTO request, String rawText) {
        if (scientificDetectorService.isScientific(rawText)) {
            log.info("Document appears to be scientific. Using Nougat OCR for reprocessing.");
            try {
                String latexText = nougatClientService.convertPdfToLatex(request.getDecodedFileBytes(), request.getFileName());
                return new FallbackResult(latexText, true);
            } catch (Exception e) {
                log.warn("Nougat OCR fallback failed, keeping original text. Reason: {}", e.getMessage());
                //TODO use retries and define the use of GPT4-vision
                throw new RuntimeException();
            }
        }
        return new FallbackResult(rawText, false);
    }


}

package com.examsolver.preprocessor.service.fallback;

import com.examsolver.preprocessor.exception.OcrProcessingException;
import com.examsolver.preprocessor.exception.PdfExtractionException;
import com.examsolver.preprocessor.service.detection.ScientificDetectorService;
import com.examsolver.preprocessor.service.strategy.PreprocessStrategy;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.enums.FallbackReasonCode;
import com.examsolver.shared.enums.FileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Component
@RequiredArgsConstructor
@Slf4j
public class FallbackOrchestrationHandler {

    private final ScientificDetectorService scientificDetector;
    private final NougatClientService nougatClient;

    /**
     * Attempts to extract text using the given strategy, and applies fallback logic
     * if scientific content is detected or if extraction fails.
     */
    public FallbackResult extractWithFallback(PreprocessRequestDTO request, PreprocessStrategy strategy) {
        final String rawText;

        try {
            rawText = strategy.extractText(request);
        } catch (PdfExtractionException e) {
            log.error("PDF text extraction failed: {}", e.getMessage());
            return FallbackResult.failed(null, FallbackReasonCode.PDFBOX_EXTRACTION_FAILED);
        } catch (OcrProcessingException e) {
            log.error("OCR text extraction failed: {}", e.getMessage());
            if (request.getFileType() == FileType.PDF) {
                return FallbackResult.failed(null, FallbackReasonCode.OCR_PDF_EXTRACTION_FAILED);
            } else {
                return FallbackResult.failed(null, FallbackReasonCode.OCR_IMAGE_EXTRACTION_FAILED);
            }
        }

        if (rawText == null || rawText.trim().isEmpty()) {
            log.warn("Text extracted is empty.");
            return FallbackResult.failed(null, FallbackReasonCode.EMPTY_EXTRACTION_RESULT);
        }

        if (!scientificDetector.isScientific(rawText)) {
            return FallbackResult.success(rawText);
        }

        try {
            final String latex = nougatClient.convertPdfToLatex(request.getDecodedFileBytes(), request.getFileName());
            return FallbackResult.nougat(latex);
        } catch (WebClientRequestException | WebClientResponseException e) {
            log.error("Nougat fallback failed. Reason: {}", e.getMessage());
            return FallbackResult.failed(rawText, FallbackReasonCode.NOUGAT_UNAVAILABLE);
        }
    }

}

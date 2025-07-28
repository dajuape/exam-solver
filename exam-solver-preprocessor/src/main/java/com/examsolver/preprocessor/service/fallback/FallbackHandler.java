package com.examsolver.preprocessor.service.fallback;

import com.examsolver.preprocessor.service.detection.ScientificDetectorService;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.enums.FallbackReasonCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class FallbackHandler {

    private final ScientificDetectorService scientificDetector;
    private final NougatClientService nougatClient;

    /**
     * Applies scientific fallback using Nougat if mathematical content is detected.
     *
     * @param request exam input
     * @param rawText text extracted via standard strategy (PDFBox, OCR, etc.)
     * @return FallbackResult with final text and a flag indicating if Nougat was used
     */
    public FallbackResult applyIfNeeded(PreprocessRequestDTO request, String rawText) {
        if (!scientificDetector.isScientific(rawText)) {
            return FallbackResult.success(rawText);
        }

        try {
            String latex = nougatClient.convertPdfToLatex(request.getDecodedFileBytes(), request.getFileName());
            return FallbackResult.nougat(latex);
        } catch (WebClientRequestException | WebClientResponseException e) {
            log.error("Nougat failed after retries. Type: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return FallbackResult.failed(rawText, FallbackReasonCode.NOUGAT_UNAVAILABLE);
        }

    }


    private String extractNougatFailureReason(Exception e) {
        if (e instanceof WebClientResponseException resEx) {
            return "HTTP " + resEx.getRawStatusCode() + " - " + resEx.getResponseBodyAsString();
        }
        if (e instanceof WebClientRequestException reqEx) {
            return "Connection error: " + reqEx.getMessage();
        }
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }


}

package com.examsolver.shared.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreprocessResponseDTO {
    private boolean success;
    private List<String> extractedText;

    private boolean fallbackRequired;
    private String fallbackReason;

    private boolean ocrWasNoisy;           // OCR had too much noise (e.g. garbled text)
    private String detectedLanguage;       // Language detected after text cleaning
    private boolean usedScientificFallback; // True if Nougat was used for scientific OCR
}

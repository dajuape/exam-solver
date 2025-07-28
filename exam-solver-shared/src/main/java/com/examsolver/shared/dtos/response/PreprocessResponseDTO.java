package com.examsolver.shared.dtos.response;

import com.examsolver.shared.enums.FallbackReasonCode;
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
    private FallbackReasonCode fallbackCode;
    private String detectedLanguage;
    private boolean userConfirmationRequired;
}

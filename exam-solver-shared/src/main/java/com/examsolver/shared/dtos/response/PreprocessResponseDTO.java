package com.examsolver.shared.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreprocessResponseDTO {
    private boolean success;
    private String extractedText;
    private boolean fallbackRequired;
    private String fallbackReason;
}

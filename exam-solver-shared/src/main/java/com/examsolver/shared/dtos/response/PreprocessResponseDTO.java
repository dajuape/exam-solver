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
}

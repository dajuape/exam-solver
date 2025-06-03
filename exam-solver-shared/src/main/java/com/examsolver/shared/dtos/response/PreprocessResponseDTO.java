package com.examsolver.shared.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreprocessResponseDTO {
    private boolean success;
    private String text;
    private boolean fallbackRequired;
    private String reason;
}

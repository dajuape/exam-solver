package com.examsolver.shared.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAIResponseDTO {
    private boolean success;
    private String resultText;
    private String error;
}

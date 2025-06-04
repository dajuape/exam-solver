package com.examsolver.shared.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIResponseDTO {
    private boolean success;
    private String resultText;
    private String error;
}

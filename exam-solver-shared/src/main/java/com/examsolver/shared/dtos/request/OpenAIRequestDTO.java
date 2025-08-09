package com.examsolver.shared.dtos.request;

import com.examsolver.shared.enums.ExamMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIRequestDTO {

    @NotBlank
    private String examId;
    @NotNull
    private ExamMode mode;

    private String detectedLanguage;
    private List<String> exercises;
    private String extractedText;

    @NotNull
    private Boolean fallbackRequired;
    private String fallbackCode;

    private String base64Image;
}

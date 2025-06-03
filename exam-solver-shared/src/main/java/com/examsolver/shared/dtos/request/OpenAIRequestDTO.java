package com.examsolver.shared.dtos.request;

import com.examsolver.shared.enums.ExamMode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIRequestDTO {
    @NotNull
    private ExamMode mode;
    @NotNull
    private String content;

    private String base64Image;    // Only if fallback = true
}

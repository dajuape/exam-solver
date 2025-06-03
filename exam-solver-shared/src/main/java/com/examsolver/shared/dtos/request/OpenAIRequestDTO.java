package com.examsolver.shared.dtos.request;

import com.examsolver.shared.enums.ExamMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIRequestDTO {

    private ExamMode mode;
    private String content;
    private String base64Image;    // Only if fallback = true
}

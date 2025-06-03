package com.examsolver.shared.dtos.response;

import com.examsolver.shared.enums.ExamStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamResponseDTO {
    private Long examId;
    private ExamStatus status;
    private String message;
}

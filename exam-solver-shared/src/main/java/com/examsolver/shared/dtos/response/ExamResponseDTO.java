package com.examsolver.shared.dtos.response;

import com.examsolver.shared.enums.ExamMode;
import com.examsolver.shared.enums.ExamStatus;
import com.examsolver.shared.enums.FallbackReasonCode;
import com.examsolver.shared.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponseDTO {
    private UUID examId;
    private ExamStatus status;
    private ExamMode mode;
    private FileType fileType;
    private String fileName;
    private String result;
    private boolean fallbackUsed;
    private FallbackReasonCode fallbackReason; 
}

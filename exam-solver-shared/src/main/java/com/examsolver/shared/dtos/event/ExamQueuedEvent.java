package com.examsolver.shared.dtos.event;

import com.examsolver.shared.enums.ExamMode;
import com.examsolver.shared.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQueuedEvent {
    private String examId;
    private ExamMode mode;
    private FileType fileType;
    private String fileName;
}

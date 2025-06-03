package com.examsolver.shared.dtos.request;

import com.examsolver.shared.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreprocessRequestDTO {
    private FileType fileType;
    private String base64File;
}

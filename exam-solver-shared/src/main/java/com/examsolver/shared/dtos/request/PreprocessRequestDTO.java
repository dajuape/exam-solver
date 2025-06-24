package com.examsolver.shared.dtos.request;

import com.examsolver.shared.enums.FileType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreprocessRequestDTO {

    @NotNull
    private FileType fileType;

    @NotNull
    private String base64File;

    @NotNull
    @Size(min = 1, max = 255)
    private String fileName;


    public byte[] getDecodedFileBytes() {
        return Base64.getDecoder().decode(this.base64File);
    }
}

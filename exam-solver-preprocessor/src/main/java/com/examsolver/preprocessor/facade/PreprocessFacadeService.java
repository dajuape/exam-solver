package com.examsolver.preprocessor.facade;

import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.dtos.response.PreprocessResponseDTO;

import java.io.IOException;

public interface PreprocessFacadeService {

    /**
     * Runs the complete preprocessing pipeline for the given request.
     *
     * @param request DTO with input file and metadata.
     * @return PreprocessResponseDTO with the processed and cleaned content.
     */
    PreprocessResponseDTO process(PreprocessRequestDTO request) throws IOException;
}
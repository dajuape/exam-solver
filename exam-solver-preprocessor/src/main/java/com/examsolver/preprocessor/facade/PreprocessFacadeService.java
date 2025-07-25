package com.examsolver.preprocessor.facade;

import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.dtos.response.PreprocessResponseDTO;

import java.io.IOException;

public interface PreprocessFacadeService {

    /**
     * Facade interface that defines the contract for executing
     * the full preprocessing pipeline of an exam input.
     *
     * <p>This service orchestrates the appropriate preprocessing strategy
     * (PDF or OCR), applies cleaning, language detection, splitting, and
     * any other analysis needed before passing the content to downstream processing.</p>
     *
     * @param request DTO containing the base64 input file and metadata
     *                such as file type and processing mode.
     * @return a {@link PreprocessResponseDTO} with the extracted text,
     *         split exercises, and processing metadata (e.g., fallback info).
     * @throws IOException if file reading or decoding fails.
     */
    PreprocessResponseDTO process(PreprocessRequestDTO request) throws IOException;

}
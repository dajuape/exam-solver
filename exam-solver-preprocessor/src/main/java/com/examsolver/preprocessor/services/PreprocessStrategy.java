package com.examsolver.preprocessor.services;

import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.dtos.response.PreprocessResponseDTO;

public interface PreprocessStrategy {

    /**
     * Defines a strategy to preprocess an exam file (PDF or image).
     * Extracts text using the appropriate method (e.g., PDFBox or OCR).
     *
     * @param requestDTO the exam file and metadata.
     * @return result with extracted text and fallback info if needed.
     */
    PreprocessResponseDTO preprocess(PreprocessRequestDTO requestDTO);
}

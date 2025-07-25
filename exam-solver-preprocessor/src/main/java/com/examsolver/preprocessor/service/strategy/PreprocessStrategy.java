package com.examsolver.preprocessor.service.strategy;

import com.examsolver.shared.dtos.request.PreprocessRequestDTO;

import java.io.IOException;

public interface PreprocessStrategy {

    /**
     * Extracts text from the given input file using the appropriate method
     * (e.g., PDF parsing or OCR).
     *
     * @param requestDTO the input request containing file metadata and content
     * @return raw extracted text (may contain noise)
     * @throws IOException in case of decoding or parsing errors
     */
    String extractText(PreprocessRequestDTO requestDTO) throws IOException;
}

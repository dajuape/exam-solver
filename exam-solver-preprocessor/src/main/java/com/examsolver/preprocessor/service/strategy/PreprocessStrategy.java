package com.examsolver.preprocessor.service.strategy;

import com.examsolver.shared.dtos.request.PreprocessRequestDTO;

import java.io.IOException;

public interface PreprocessStrategy {

    /**
     * Extracts text from a file (PDF, image, etc.).
     *
     * @param requestDTO the request with base64 and metadata.
     * @return raw extracted text.
     */
    String extractText(PreprocessRequestDTO requestDTO) throws IOException;
}

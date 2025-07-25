package com.examsolver.preprocessor.service.detection;


public interface NoiseDetectionService {

    /**
     * Heuristically determines whether the input text is too noisy
     * to be reliably processed.
     *
     * @param text the extracted text to evaluate
     * @return true if the text is likely too noisy (e.g., due to OCR artifacts)
     */
    boolean isTooNoisy(String text);
}

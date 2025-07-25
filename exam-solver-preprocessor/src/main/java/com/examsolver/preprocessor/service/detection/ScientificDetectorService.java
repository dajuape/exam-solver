package com.examsolver.preprocessor.service.detection;

public interface ScientificDetectorService {

    /**
     * Evaluates whether the input text likely represents scientific/mathematical content.
     *
     * @param text the text to analyze
     * @return true if mathematical constructs or LaTeX expressions are detected
     */
    boolean isScientific(String text);
}

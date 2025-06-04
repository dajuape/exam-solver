package com.examsolver.preprocessor.services;

public interface MathDetectionService {
    
    /**
     * Detects and wraps math expressions in markdown blocks.
     *
     * @param input Text to analyze.
     * @return Text with math expressions wrapped.
     */
    String wrapMathExpressions(String input);
}

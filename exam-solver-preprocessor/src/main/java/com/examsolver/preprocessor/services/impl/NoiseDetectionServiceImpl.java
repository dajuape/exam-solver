package com.examsolver.preprocessor.services.impl;

import com.examsolver.preprocessor.services.NoiseDetectionService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class NoiseDetectionServiceImpl implements NoiseDetectionService {

    private static final double NOISE_RATIO_THRESHOLD = 0.02;
    private static final double MALFORMED_WORD_RATIO_THRESHOLD = 0.25;
    private static final double MALFORMED_WORD_MIN_LENGTH = 3;
    private static final double MALFORMED_CHAR_RATIO = 0.4;

    @Override
    public boolean isTooNoisy(String text) {
        long nonPrintable = text.chars().filter(c -> c < 32 || c > 126).count();
        long garbageChars = text.chars().filter(c -> "!@#$%^&*_=+[]<>¿¡~".indexOf(c) >= 0).count();
        double noiseRatio = (nonPrintable + garbageChars) / (double) text.length();

        String[] words = text.split("\\s+");
        long totalWords = words.length;
        long malformedWords = Arrays.stream(words)
                .filter(w -> w.length() > MALFORMED_WORD_MIN_LENGTH &&
                        w.replaceAll("[a-zA-ZáéíóúñÁÉÍÓÚÑ]", "").length() > w.length() * MALFORMED_CHAR_RATIO)
                .count();
        double malformedRatio = (double) malformedWords / totalWords;

        return noiseRatio > NOISE_RATIO_THRESHOLD || malformedRatio > MALFORMED_WORD_RATIO_THRESHOLD;
    }
}

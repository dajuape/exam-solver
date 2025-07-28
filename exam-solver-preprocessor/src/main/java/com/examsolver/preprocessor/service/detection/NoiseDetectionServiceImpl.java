package com.examsolver.preprocessor.service.detection;

import com.examsolver.preprocessor.config.NoiseDetectionProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Implementation of {@link NoiseDetectionService} that uses heuristics
 * to detect OCR noise in extracted text.
 *
 * <p>Checks include:
 * <ul>
 *   <li>Ratio of non-printable or garbage characters (e.g. symbols)</li>
 *   <li>Ratio of malformed words with excessive non-alphabetic characters</li>
 * </ul>
 *
 * <p>If either metric exceeds a defined threshold, the text is considered too noisy.</p>
 */
@Service
@AllArgsConstructor
public class NoiseDetectionServiceImpl implements NoiseDetectionService {

    private final NoiseDetectionProperties noiseDetectionProperties;

    @Override
    public boolean isTooNoisy(String text) {
        long nonPrintable = text.chars().filter(c -> c < 32 || c > 126).count();
        long garbageChars = text.chars().filter(c -> "!@#$%^&*_=+[]<>¿¡~".indexOf(c) >= 0).count();
        double noiseRatio = (nonPrintable + garbageChars) / (double) text.length();

        String[] words = text.split("\\s+");
        long totalWords = words.length;
        long malformedWords = Arrays.stream(words)
                .filter(w -> w.length() > noiseDetectionProperties.getMalformedWordMinLength() &&
                        w.replaceAll("[a-zA-ZáéíóúñÁÉÍÓÚÑ]", "").length() > w.length() * noiseDetectionProperties.getMalformedCharRatio())
                .count();
        double malformedRatio = (double) malformedWords / totalWords;

        return noiseRatio > noiseDetectionProperties.getNoiseRatioThreshold() || malformedRatio > noiseDetectionProperties.getMalformedWordRatioThreshold();
    }
}

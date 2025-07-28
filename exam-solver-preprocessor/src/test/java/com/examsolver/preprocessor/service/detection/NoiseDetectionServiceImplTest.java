package com.examsolver.preprocessor.service.detection;

import com.examsolver.preprocessor.config.NoiseDetectionProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoiseDetectionServiceImplTest {

    private NoiseDetectionServiceImpl noiseDetectionService;
    private NoiseDetectionProperties props;
    @BeforeEach
    void setUp() {
        props = new NoiseDetectionProperties();
        props.setNoiseRatioThreshold(0.02);
        props.setMalformedWordRatioThreshold(0.25);
        props.setMalformedCharRatio(0.4);
        props.setMalformedWordMinLength(3);
        noiseDetectionService = new NoiseDetectionServiceImpl(props);
    }


    @Test
    void shouldReturnFalseForCleanTextWithTildes() {
        String cleanText = "Esta es una oración perfectamente legible con acentos y eñes.";
        assertFalse(noiseDetectionService.isTooNoisy(cleanText));
    }



    @Test
    void shouldDetectNoiseBasedOnSymbols() {
        String noisyText = "Th!s @rt1cle $$%% ha$ %o m@ny symb0ls &&& that #### m@ke i++ !@@## nois¥¥¥¥";
        assertTrue(noiseDetectionService.isTooNoisy(noisyText));
    }

    @Test
    void shouldDetectNoiseBasedOnMalformedWords() {
        String text = "ValidWord ab@# 1234 %$#*! lkjlkj lkj1234 valid again noise!!!";
        assertTrue(noiseDetectionService.isTooNoisy(text));
    }

    @Test
    void shouldReturnFalseForShortCleanInput() {
        String shortClean = "Hola mundo";
        assertFalse(noiseDetectionService.isTooNoisy(shortClean));
    }

    @Test
    void shouldHandleEmptyInputAsNotNoisy() {
        assertFalse(noiseDetectionService.isTooNoisy(""));
    }
}

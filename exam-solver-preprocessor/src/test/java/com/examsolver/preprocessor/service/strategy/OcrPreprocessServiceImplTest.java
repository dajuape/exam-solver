package com.examsolver.preprocessor.service.strategy;

import com.examsolver.preprocessor.config.OcrProperties;
import com.examsolver.preprocessor.exception.OcrProcessingException;
import com.examsolver.preprocessor.service.detection.LanguageDetectionService;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.enums.FileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OcrPreprocessServiceImplTest {

    private OcrPreprocessServiceImpl ocrService;
    private LanguageDetectionService languageDetectionService;
    private OcrProperties ocrProperties;

    @BeforeEach
    void setUp() {
        languageDetectionService = mock(LanguageDetectionService.class);
        ocrProperties = mock(OcrProperties.class);

        when(ocrProperties.getDefaultLang()).thenReturn("eng");
        when(ocrProperties.getTessdataPath()).thenReturn("/usr/share/tesseract-ocr/5/tessdata");
        when(ocrProperties.getLanguageCodeMap()).thenReturn(Map.of(
                "es", "spa",
                "en", "eng",
                "fr", "fra",
                "de", "deu"
        ));

        ocrService = new OcrPreprocessServiceImpl(languageDetectionService, ocrProperties);
    }

    @Test
    void shouldPerformOcrOnValidImage() {
        // Arrange
        byte[] imageBytes = loadTestImage("test-ocr.jpeg");

        PreprocessRequestDTO request = createRequest(imageBytes, FileType.IMAGE);

        when(languageDetectionService.detectLanguage(anyString())).thenReturn("unknown");

        // Act
        String result = ocrService.extractText(request);

        // Assert
        assertNotNull(result);
        assertFalse(result.trim().isEmpty(), "OCR debería devolver texto");
        System.out.println("OCR Result (1st pass): \n" + result);
    }

    @Test
    void shouldPerformOcrOnScannedPdf() {
        // Arrange
        byte[] pdfBytes = loadTestScannedPdf("scanned-test.pdf");

        PreprocessRequestDTO request = createRequest(pdfBytes, FileType.PDF);

        when(languageDetectionService.detectLanguage(anyString())).thenReturn("unknown");

        // Act
        String result = ocrService.extractText(request);

        // Assert
        assertNotNull(result);
        assertFalse(result.trim().isEmpty(), "OCR sobre PDF debería devolver texto");
        System.out.println("OCR PDF result:\n" + result);
    }


    @Test
    void shouldRetryOcrWhenLanguageChanges() {
        // Arrange
        byte[] imageBytes = loadTestImage("test-ocr.jpeg");

        PreprocessRequestDTO request = createRequest(imageBytes, FileType.IMAGE);

        // Simula que el OCR en inglés devuelve texto en español → detecta "es"
        when(languageDetectionService.detectLanguage(anyString())).thenReturn("es");

        // Act
        String result = ocrService.extractText(request);

        // Assert
        assertNotNull(result);
        assertFalse(result.trim().isEmpty(), "OCR debería devolver texto tras retry");
        verify(languageDetectionService).detectLanguage(anyString());
        System.out.println("OCR Result (retry with 'spa'): \n" + result);
    }

    @Test
    void shouldThrowExceptionForInvalidImage() {
        // Arrange
        byte[] invalid = "invalid-image".getBytes();
        PreprocessRequestDTO request = createRequest(invalid, FileType.IMAGE);

        // Act & Assert
        OcrProcessingException ex = assertThrows(
                OcrProcessingException.class,
                () -> ocrService.extractText(request)
        );

        assertTrue(ex.getMessage().contains("Failed to decode image"));
    }


    @Test
    void shouldThrowExceptionIfTesseractFails() {
        // Arrange: PDF con contenido inválido
        byte[] invalidPdf = "not-a-pdf".getBytes();
        PreprocessRequestDTO request = createRequest(invalidPdf, FileType.PDF);

        // Act & Assert
        OcrProcessingException ex = assertThrows(
                OcrProcessingException.class,
                () -> ocrService.extractText(request)
        );

        assertTrue(ex.getMessage().contains("OCR failed for scanned PDF"));
    }

    @Test
    void shouldThrowExceptionIfTesseractFailsOnPdf() {
        byte[] invalidPdf = "this is not a real pdf".getBytes();
        PreprocessRequestDTO request = createRequest(invalidPdf, FileType.PDF);

        OcrProcessingException ex = assertThrows(
                OcrProcessingException.class,
                () -> ocrService.extractText(request)
        );

        assertTrue(ex.getMessage().contains("OCR failed for scanned PDF"));
    }


    // ==== Helpers ====

    private PreprocessRequestDTO createRequest(byte[] bytes, FileType type) {
        PreprocessRequestDTO dto = new PreprocessRequestDTO();
        dto.setFileType(type);
        dto.setFileName("test-file");
        dto.setBase64File(Base64.getEncoder().encodeToString(bytes));
        return dto;
    }

    private byte[] loadTestImage(String filename) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("test_images/" + filename)) {
            if (is == null) {
                throw new IllegalArgumentException("Test image wasn't found: " + filename);
            }
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading test image", e);
        }
    }

    private byte[] loadTestScannedPdf(String filename) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("test_pdfs/" + filename)) {
            if (is == null) {
                throw new IllegalArgumentException("Test image wasn't found: " + filename);
            }
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading test image", e);
        }
    }
}

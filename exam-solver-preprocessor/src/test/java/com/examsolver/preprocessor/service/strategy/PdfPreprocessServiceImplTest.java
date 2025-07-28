package com.examsolver.preprocessor.service.strategy;

import com.examsolver.preprocessor.exception.PdfExtractionException;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.enums.FileType;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PdfPreprocessServiceImplTest {

    private PdfPreprocessServiceImpl pdfPreprocessService;

    @BeforeEach
    void setUp() {
        pdfPreprocessService = new PdfPreprocessServiceImpl();
    }

    @Test
    @SneakyThrows
    void shouldExtractTextFromRealPdfFile() {
        // Arrange
        InputStream is = getClass().getResourceAsStream("/test_pdfs/no-scanned-exam.pdf");
        assertNotNull(is, "PDF test file not found");

        byte[] pdfBytes = is.readAllBytes();
        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

        PreprocessRequestDTO requestDTO = PreprocessRequestDTO.builder()
                .base64File(base64Pdf)
                .fileType(FileType.PDF)
                .fileName("no-scanned-exam.pdf")
                .build();

        // Act
        String result = pdfPreprocessService.extractText(requestDTO);

        // Assert
        assertNotNull(result);
        assertFalse(result.trim().isEmpty(), "Extracted text should not be empty for a text-based PDF");
    }


    @Test
    void shouldThrowPdfExtractionExceptionWhenPdfIsCorrupted() {
        // Arrange
        byte[] invalidPdf = "Not a real PDF content".getBytes();
        String base64Pdf = Base64.getEncoder().encodeToString(invalidPdf);


        PreprocessRequestDTO requestDTO = PreprocessRequestDTO.builder()
                .base64File(base64Pdf)
                .build();

        // Act & Assert
        PdfExtractionException exception = assertThrows(
                PdfExtractionException.class,
                () -> pdfPreprocessService.extractText(requestDTO)
        );

        assertEquals("PDFBox failed to extract text", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    /**
     * Helper method to create a simple PDF in memory using PDFBox
     */
    @SneakyThrows
    private byte[] createSimplePdf(String text) {
        try (PDDocument document = new PDDocument()) {
            var page = new org.apache.pdfbox.pdmodel.PDPage();
            document.addPage(page);

            try (var contentStream = new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText(text);
                contentStream.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}

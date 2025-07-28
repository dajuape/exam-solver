package com.examsolver.preprocessor.service.analysis;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class PdfContentAnalyzerServiceImplTest {

    private PdfContentAnalyzerServiceImpl analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new PdfContentAnalyzerServiceImpl();
    }

    @Test
    void shouldReturnFalseForRealTextBasedPdf() throws IOException {
        InputStream is = getClass().getResourceAsStream("/test_pdfs/no-scanned-exam.pdf");
        assertNotNull(is, "Test PDF not found");

        byte[] pdfBytes = is.readAllBytes();
        boolean result = analyzer.isScanned(pdfBytes);

        assertFalse(result);
    }


    @Test
    void shouldReturnTrueForEmptyPdf() throws IOException {
        byte[] pdfBytes = createTextPdf("");
        boolean result = analyzer.isScanned(pdfBytes);
        assertTrue(result);
    }

    @Test
    void shouldReturnTrueIfPdfCannotBeRead() {
        byte[] corrupted = "not a pdf".getBytes();
        boolean result = analyzer.isScanned(corrupted);
        assertTrue(result);
    }

    private byte[] createTextPdf(String text) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            if (text != null && !text.isEmpty()) {
                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText(text);
                    contentStream.endText();
                }
            }

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                doc.save(out);
                return out.toByteArray();
            }
        }
    }
}

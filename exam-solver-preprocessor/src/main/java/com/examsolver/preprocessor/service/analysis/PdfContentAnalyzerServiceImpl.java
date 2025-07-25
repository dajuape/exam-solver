package com.examsolver.preprocessor.service.analysis;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Implementation of {@link PdfContentAnalyzerService} that uses Apache PDFBox
 * to extract text and infer whether a PDF is scanned.
 *
 * <p>A scanned PDF typically yields no extractable text via PDFBox,
 * which is used here as a heuristic to trigger OCR fallback.</p>
 */
@Slf4j
@Service
public class PdfContentAnalyzerServiceImpl implements PdfContentAnalyzerService {

    @Override
    public boolean isScanned(byte[] pdfBytes) {
        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            String text = new PDFTextStripper().getText(doc);
            return text.trim().isEmpty();
        } catch (IOException e) {
            log.warn("Failed to analyze PDF. Assuming it's scanned. Reason: {}", e.getMessage());
            return true;
        }
    }
}

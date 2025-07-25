package com.examsolver.preprocessor.service.analysis;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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

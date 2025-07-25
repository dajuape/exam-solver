package com.examsolver.preprocessor.service.analysis;

public interface PdfContentAnalyzerService {

    /**
     * Checks if the provided PDF contains extractable text.
     *
     * <p>If no text is found, the PDF is assumed to be scanned and will require OCR processing.</p>
     *
     * @param pdfBytes the raw bytes of the PDF file
     * @return true if the PDF appears to be scanned (no extractable text), false otherwise
     */
    boolean isScanned(byte[] pdfBytes);
}

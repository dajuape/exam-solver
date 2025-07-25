package com.examsolver.preprocessor.service.fallback;

public interface NougatClientService {
    String convertPdfToLatex(byte[] pdfBytes, String fileName);
}

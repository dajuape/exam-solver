package com.examsolver.preprocessor.services;

public interface NougatClientService {
    String convertPdfToLatex(byte[] pdfBytes, String fileName);
}

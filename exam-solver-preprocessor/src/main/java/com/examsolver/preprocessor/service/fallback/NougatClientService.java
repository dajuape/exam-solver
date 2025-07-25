package com.examsolver.preprocessor.service.fallback;

public interface NougatClientService {

    /**
     * Sends a PDF file to the Nougat service and retrieves the LaTeX output.
     *
     * @param pdfBytes  the raw bytes of the PDF file
     * @param fileName  the original file name (used as part of the multipart form)
     * @return the LaTeX-formatted string returned by Nougat
     */
    String convertPdfToLatex(byte[] pdfBytes, String fileName);
}

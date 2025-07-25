package com.examsolver.preprocessor.service.cleaner;

public interface TextCleaningService {

    /**
     * Cleans and normalizes raw text extracted from scanned or digital exam files.
     *
     * <p>This includes removal of OCR noise, page artifacts, repeated headers/footers,
     * line joining, and preparation for exercise splitting.</p>
     *
     * @param input raw extracted text
     * @return cleaned and normalized text suitable for downstream processing
     */
    String clean(String input);
}

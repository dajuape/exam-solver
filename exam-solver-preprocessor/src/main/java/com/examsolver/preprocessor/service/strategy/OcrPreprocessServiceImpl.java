package com.examsolver.preprocessor.service.strategy;

import com.examsolver.preprocessor.config.OcrProperties;
import com.examsolver.preprocessor.exception.OcrProcessingException;
import com.examsolver.preprocessor.service.detection.LanguageDetectionService;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.enums.FileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Strategy implementation for processing scanned PDFs or images using Tesseract OCR.
 *
 * <p>The service performs OCR on the input file, initially using English, and
 * then retries with the detected language if it's different and supported.</p>
 *
 * <p>Supports language fallback and uses {@link Tesseract} configured with
 * language-specific models.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OcrPreprocessServiceImpl implements PreprocessStrategy {

    private static final String DEFAULT_LANG = "eng";
    private static final Map<String, String> LANGUAGE_CODE_MAP = Map.of(
            "es", "spa",
            "en", "eng",
            "fr", "fra",
            "de", "deu"
    );
    private final LanguageDetectionService languageDetectionService;
    private final OcrProperties ocrProperties;

    @Override
    public String extractText(PreprocessRequestDTO requestDTO) {
        final byte[] bytes = requestDTO.getDecodedFileBytes();

        String text = requestDTO.getFileType() == FileType.PDF
                ? doOcrPdf(bytes, DEFAULT_LANG)
                : doOcrImage(bytes, DEFAULT_LANG);


        final String detectedLang = mapToTesseractLang(languageDetectionService.detectLanguage(text));

        log.info("Detected language: {}", detectedLang);

        if (!"unknown".equals(detectedLang) && !DEFAULT_LANG.equals(detectedLang)) {
            try {
                log.info("Reprocessing OCR with detected language '{}'", detectedLang);
                text = requestDTO.getFileType() == FileType.PDF
                        ? doOcrPdf(bytes, detectedLang)
                        : doOcrImage(bytes, detectedLang);
            } catch (Exception e) {
                log.warn("OCR reprocessing with '{}' failed. Keeping original OCR text. Reason: {}", detectedLang, e.getMessage());
            }
        }

        return text.trim();
    }

    private String doOcrPdf(byte[] bytes, String lang) {
        try (final PDDocument document = PDDocument.load(new ByteArrayInputStream(bytes))) {

            final PDFRenderer renderer = new PDFRenderer(document);
            final Tesseract tesseract = createTesseract(lang);
            final StringBuilder sb = new StringBuilder();

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                final BufferedImage image = renderer.renderImageWithDPI(i, 300);
                sb.append(tesseract.doOCR(image)).append("\n");
            }

            return sb.toString();
        } catch (IOException | TesseractException e) {
            throw new OcrProcessingException("OCR failed for scanned PDF with language '" + lang + "': " + e.getMessage(), e);
        }
    }

    private String doOcrImage(byte[] bytes, String lang) {
        try {
            final BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                throw new OcrProcessingException("Failed to decode image for OCR", null);
            }
            return createTesseract(lang).doOCR(image);
        } catch (IOException | TesseractException e) {
            throw new OcrProcessingException("OCR failed for image with language '" + lang + "'", e);
        }
    }

    private Tesseract createTesseract(String lang) {
        final Tesseract tesseract = new Tesseract();
        tesseract.setLanguage(lang);
        tesseract.setDatapath(ocrProperties.getTessdataPath());
        return tesseract;
    }

    private String mapToTesseractLang(String lang) {
        return LANGUAGE_CODE_MAP.getOrDefault(lang, lang);
    }
}

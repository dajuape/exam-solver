package com.examsolver.preprocessor.service.strategy;

import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Strategy implementation for processing digital PDFs using Apache PDFBox.
 *
 * <p>This strategy assumes the PDF is not scanned and contains extractable
 * text. It uses {@link PDFTextStripper} to retrieve the textual content.</p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class PdfPreprocessServiceImpl implements PreprocessStrategy {

    @Override
    public String extractText(PreprocessRequestDTO requestDTO) {

        final byte[] decodedBytes = Base64.getDecoder().decode(requestDTO.getBase64File());

        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(decodedBytes))) {

            final PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);


        } catch (IOException e) {
            log.error("Error preprocessing a PDF.", e);
            throw new RuntimeException("Error preprocessing a PDF", e);
        }
    }

}

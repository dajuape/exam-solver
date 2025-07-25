package com.examsolver.preprocessor.service.fallback;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implementation of {@link NougatClientService} that sends PDF files to
 * the Nougat OCR service via HTTP using Spring WebClient.
 *
 * <p>The service expects the Nougat API to expose a POST endpoint
 * at <code>/predict/</code> that accepts multipart/form-data with a PDF file.</p>
 *
 * <p>This method is synchronous and uses {@link WebClient#block()} to retrieve
 * the LaTeX output as a plain string.</p>
 *
 * <p>Retry behavior is applied via Resilience4j using the "nougatServiceRetry" config.</p>
 */
@Service
@RequiredArgsConstructor
public class NougatClientServiceImpl implements NougatClientService {

    private final WebClient nougatWebClient;

    @Override
    @Retry(name = "nougatServiceRetry")
    public String convertPdfToLatex(byte[] pdfBytes, String fileName) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder
                .part("file", new ByteArrayResource(pdfBytes) {
                    @Override
                    public String getFilename() {
                        return fileName;
                    }
                })
                .contentType(MediaType.APPLICATION_PDF);

        return nougatWebClient.post()
                .uri("/predict/")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

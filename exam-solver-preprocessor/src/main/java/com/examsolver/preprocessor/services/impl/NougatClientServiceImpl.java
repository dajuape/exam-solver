package com.examsolver.preprocessor.services.impl;

import com.examsolver.preprocessor.services.NougatClientService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

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

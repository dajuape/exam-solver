package com.examsolver.preprocessor.service.fallback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NougatClientServiceImplTest {

    private WebClient mockWebClient;
    private WebClient.RequestBodyUriSpec mockRequest;
    private WebClient.RequestBodySpec mockRequestBodySpec;
    private WebClient.RequestHeadersSpec mockHeadersSpec;
    private WebClient.ResponseSpec mockResponseSpec;

    private NougatClientServiceImpl nougatClientService;

    @BeforeEach
    void setUp() {
        mockWebClient = mock(WebClient.class);
        mockRequest = mock(WebClient.RequestBodyUriSpec.class);
        mockRequestBodySpec = mock(WebClient.RequestBodySpec.class);
        mockHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(mockWebClient.post()).thenReturn(mockRequest);
        when(mockRequest.uri("/predict/")).thenReturn(mockRequestBodySpec);
        when(mockRequestBodySpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(mockRequestBodySpec);
        when(mockRequestBodySpec.body(any(BodyInserters.MultipartInserter.class))).thenReturn(mockHeadersSpec);
        when(mockHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Mocked LaTeX"));

        nougatClientService = new NougatClientServiceImpl(mockWebClient);
    }

    @Test
    void shouldSendPdfAndReturnLatexString() {
        byte[] pdfBytes = "dummy content".getBytes();
        String fileName = "test.pdf";

        String result = nougatClientService.convertPdfToLatex(pdfBytes, fileName);

        assertEquals("Mocked LaTeX", result);

        verify(mockWebClient).post();
        verify(mockRequest).uri("/predict/");
        verify(mockRequestBodySpec).contentType(MediaType.MULTIPART_FORM_DATA);
        verify(mockRequestBodySpec).body(any(BodyInserters.MultipartInserter.class));
        verify(mockHeadersSpec).retrieve();
        verify(mockResponseSpec).bodyToMono(String.class);
    }
}

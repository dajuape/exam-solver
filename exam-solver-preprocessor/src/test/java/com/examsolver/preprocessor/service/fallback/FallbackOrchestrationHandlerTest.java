package com.examsolver.preprocessor.service.fallback;

import com.examsolver.preprocessor.exception.OcrProcessingException;
import com.examsolver.preprocessor.exception.PdfExtractionException;
import com.examsolver.preprocessor.service.detection.ScientificDetectorService;
import com.examsolver.preprocessor.service.strategy.PreprocessStrategy;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.enums.FallbackReasonCode;
import com.examsolver.shared.enums.FileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.http.HttpMethod;

import java.net.URI;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FallbackOrchestrationHandlerTest {

    private ScientificDetectorService scientificDetector;
    private NougatClientService nougatClient;
    private PreprocessStrategy strategy;
    private FallbackOrchestrationHandler orchestrationHandler;

    @BeforeEach
    void setUp() {
        scientificDetector = mock(ScientificDetectorService.class);
        nougatClient = mock(NougatClientService.class);
        strategy = mock(PreprocessStrategy.class);
        orchestrationHandler = new FallbackOrchestrationHandler(scientificDetector, nougatClient);
    }

    @Test
    void shouldReturnPdfboxFailureIfPdfExtractionThrows() {
        PreprocessRequestDTO request = mock(PreprocessRequestDTO.class);
        when(strategy.extractText(request)).thenThrow(new PdfExtractionException("pdf error", null));

        FallbackResult result = orchestrationHandler.extractWithFallback(request, strategy);

        assertTrue(result.usedNougatFallback());
        assertTrue(result.nougatFailed());
        assertNull(result.text());
        assertEquals(FallbackReasonCode.PDFBOX_EXTRACTION_FAILED, result.fallbackCode());
    }

    @Test
    void shouldReturnOcrPdfFailureIfOcrThrowsForPdf() {
        PreprocessRequestDTO request = mock(PreprocessRequestDTO.class);
        when(request.getFileType()).thenReturn(FileType.PDF);
        when(strategy.extractText(request)).thenThrow(new OcrProcessingException("ocr error", null));

        FallbackResult result = orchestrationHandler.extractWithFallback(request, strategy);

        assertTrue(result.usedNougatFallback());
        assertTrue(result.nougatFailed());
        assertNull(result.text());
        assertEquals(FallbackReasonCode.OCR_PDF_EXTRACTION_FAILED, result.fallbackCode());
    }

    @Test
    void shouldReturnOcrImageFailureIfOcrThrowsForImage() {
        PreprocessRequestDTO request = mock(PreprocessRequestDTO.class);
        when(request.getFileType()).thenReturn(FileType.IMAGE);
        when(strategy.extractText(request)).thenThrow(new OcrProcessingException("ocr error", null));

        FallbackResult result = orchestrationHandler.extractWithFallback(request, strategy);

        assertTrue(result.usedNougatFallback());
        assertTrue(result.nougatFailed());
        assertNull(result.text());
        assertEquals(FallbackReasonCode.OCR_IMAGE_EXTRACTION_FAILED, result.fallbackCode());
    }

    @Test
    void shouldReturnEmptyExtractionFailureIfTextIsEmpty() {
        PreprocessRequestDTO request = mock(PreprocessRequestDTO.class);
        when(strategy.extractText(request)).thenReturn("   ");

        FallbackResult result = orchestrationHandler.extractWithFallback(request, strategy);

        assertTrue(result.usedNougatFallback());
        assertTrue(result.nougatFailed());
        assertNull(result.text());
        assertEquals(FallbackReasonCode.EMPTY_EXTRACTION_RESULT, result.fallbackCode());
    }

    @Test
    void shouldReturnSuccessIfTextIsNotScientific() {
        PreprocessRequestDTO request = mock(PreprocessRequestDTO.class);
        String rawText = "This is a normal text";
        when(strategy.extractText(request)).thenReturn(rawText);
        when(scientificDetector.isScientific(rawText)).thenReturn(false);

        FallbackResult result = orchestrationHandler.extractWithFallback(request, strategy);

        assertEquals(rawText, result.text());
        assertFalse(result.usedNougatFallback());
        assertFalse(result.nougatFailed());
        assertNull(result.fallbackCode());
    }

    @Test
    void shouldReturnNougatIfScientificAndSuccessful() {
        PreprocessRequestDTO request = mock(PreprocessRequestDTO.class);
        byte[] bytes = "bytes".getBytes();
        String fileName = "file.pdf";

        when(strategy.extractText(request)).thenReturn("E = mc^2");
        when(scientificDetector.isScientific(any())).thenReturn(true);
        when(request.getDecodedFileBytes()).thenReturn(bytes);
        when(request.getFileName()).thenReturn(fileName);
        when(nougatClient.convertPdfToLatex(bytes, fileName)).thenReturn("latex text");

        FallbackResult result = orchestrationHandler.extractWithFallback(request, strategy);

        assertEquals("latex text", result.text());
        assertTrue(result.usedNougatFallback());
        assertFalse(result.nougatFailed());
        assertNull(result.fallbackCode());
    }

    @Test
    void shouldReturnNougatFailureIfNougatClientFails() {
        PreprocessRequestDTO request = mock(PreprocessRequestDTO.class);
        byte[] bytes = "pdf".getBytes();
        String fileName = "file.pdf";

        when(strategy.extractText(request)).thenReturn("math");
        when(scientificDetector.isScientific(any())).thenReturn(true);
        when(request.getDecodedFileBytes()).thenReturn(bytes);
        when(request.getFileName()).thenReturn(fileName);
        when(nougatClient.convertPdfToLatex(bytes, fileName))
                .thenThrow(new WebClientRequestException(
                        new UnknownHostException("Simulated connection issue"),
                        HttpMethod.POST,
                        URI.create("http://fake-url"),
                        new HttpHeaders()
                ));


        FallbackResult result = orchestrationHandler.extractWithFallback(request, strategy);

        assertEquals("math", result.text());
        assertTrue(result.usedNougatFallback());
        assertTrue(result.nougatFailed());
        assertEquals(FallbackReasonCode.NOUGAT_UNAVAILABLE, result.fallbackCode());
    }
}

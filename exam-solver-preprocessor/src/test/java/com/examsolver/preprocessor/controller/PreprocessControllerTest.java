package com.examsolver.preprocessor.controller;

import com.examsolver.preprocessor.facade.PreprocessFacadeService;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.dtos.response.PreprocessResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PreprocessControllerTest {

    private PreprocessFacadeService preprocessFacadeService;
    private PreprocessController preprocessController;

    @BeforeEach
    void setUp() {
        preprocessFacadeService = mock(PreprocessFacadeService.class);
        preprocessController = new PreprocessController(preprocessFacadeService);
    }

    @Test
    void testHandle_ReturnsOkResponse() throws IOException {
        // Given a valid request and a mocked response
        PreprocessRequestDTO request = new PreprocessRequestDTO(); // Assumes a no-arg constructor or builder
        PreprocessResponseDTO response = new PreprocessResponseDTO(); // Assumes a no-arg constructor or builder

        when(preprocessFacadeService.process(request)).thenReturn(response);

        // When
        ResponseEntity<PreprocessResponseDTO> entity = preprocessController.handle(request);

        // Then
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(response, entity.getBody());
        verify(preprocessFacadeService, times(1)).process(request);
    }

    @Test
    void testHandle_ThrowsIOException() throws IOException {
        // Given a request that triggers an IOException
        PreprocessRequestDTO request = new PreprocessRequestDTO();

        when(preprocessFacadeService.process(ArgumentMatchers.any())).thenThrow(new IOException("Test IO error"));

        // When/Then
        IOException thrown = assertThrows(IOException.class, () -> preprocessController.handle(request));
        assertEquals("Test IO error", thrown.getMessage());
        verify(preprocessFacadeService, times(1)).process(request);
    }
}

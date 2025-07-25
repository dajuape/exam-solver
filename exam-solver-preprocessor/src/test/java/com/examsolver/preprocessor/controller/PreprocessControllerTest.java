package com.examsolver.preprocessor.controller;

import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.enums.FileType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreprocessController.class)
class PreprocessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnMockedPreprocessResponse() throws Exception {
        PreprocessRequestDTO request = PreprocessRequestDTO.builder()
                .fileType(FileType.PDF)
                .base64File("dGVzdA==") // base64 de "test"
                .fileName("examen.pdf")
                .build();

        mockMvc.perform(post("/preprocess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.extractedText").value("mocked text"))
                .andExpect(jsonPath("$.fallbackRequired").value(false))
                .andExpect(jsonPath("$.fallbackReason").doesNotExist());
    }
}

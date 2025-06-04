package com.examsolver.preprocessor.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PreprocessController.class)
class PreprocessControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Request validation tests")
    class InvalidRequests {

        @Test
        void shouldReturn400WhenFileTypeIsMissing() throws Exception {
            var json = """
                    {
                        "base64File": "dGVzdA==",
                        "fileName": "examen.jpg"
                    }
                    """;

            mockMvc.perform(post("/preprocess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenBase64FileIsMissing() throws Exception {
            var json = """
                    {
                        "fileType": "IMAGE",
                        "fileName": "foto.jpg"
                    }
                    """;

            mockMvc.perform(post("/preprocess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenFileNameIsMissing() throws Exception {
            var json = """
                    {
                        "fileType": "PDF",
                        "base64File": "dGVzdA=="
                    }
                    """;

            mockMvc.perform(post("/preprocess")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }
}

package com.examsolver.preprocessor.controllers;

import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.dtos.response.PreprocessResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preprocess")
public class PreprocessController {

    @PostMapping
    public ResponseEntity<PreprocessResponseDTO> handle(@Valid @RequestBody PreprocessRequestDTO request) {
        var response = PreprocessResponseDTO.builder()
                .success(true)
                .extractedText("mocked text")
                .fallbackRequired(false)
                .build();

        return ResponseEntity.ok(response);
    }

}
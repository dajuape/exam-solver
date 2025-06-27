package com.examsolver.preprocessor.controllers;

import com.examsolver.preprocessor.services.PreprocessFacadeService;
import com.examsolver.shared.dtos.request.PreprocessRequestDTO;
import com.examsolver.shared.dtos.response.PreprocessResponseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/preprocess")
@AllArgsConstructor
public class PreprocessController {

    private final PreprocessFacadeService preprocessFacadeService;

    @PostMapping
    public ResponseEntity<PreprocessResponseDTO> handle(@Valid @RequestBody PreprocessRequestDTO request) throws IOException {


        final PreprocessResponseDTO response = preprocessFacadeService.process(request);

        return ResponseEntity.ok(response);
    }
}

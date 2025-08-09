package com.examsolver.openai.controller;

import com.examsolver.openai.dto.client.OpenAiRequest;
import com.examsolver.openai.dto.service.OpenAiProcessResponseDTO;
import com.examsolver.openai.service.processing.OpenAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openai")
@RequiredArgsConstructor
public class OpenAiController {

    private final OpenAiService openAiService;

    @PostMapping("/process")
    public ResponseEntity<OpenAiProcessResponseDTO> processExam(
            @RequestBody @Valid OpenAiRequest request) {

        return ResponseEntity.ok(openAiService.processExam(request));
    }
}

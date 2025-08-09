package com.examsolver.openai.service.processing;

import com.examsolver.openai.dto.client.OpenAiRequest;

public interface OpenAiService {

    com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO processExam(OpenAiRequest preprocessResponseDTO);
}

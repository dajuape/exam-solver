package com.examsolver.openai.service.processing;

import com.examsolver.openai.dto.client.OpenAiRequest;
import com.examsolver.openai.dto.service.OpenAiProcessResponseDTO;

public interface OpenAiService {

    OpenAiProcessResponseDTO processExam(OpenAiRequest preprocessResponseDTO);
}

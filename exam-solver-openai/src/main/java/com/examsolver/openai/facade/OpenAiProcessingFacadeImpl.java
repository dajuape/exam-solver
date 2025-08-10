package com.examsolver.openai.facade;

import com.examsolver.openai.service.processing.OpenAiService;
import com.examsolver.openai.service.validation.RequestValidatorService;
import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiProcessingFacadeImpl implements OpenAiProcessingFacade {

    private final RequestValidatorService validator;
    private final OpenAiService openAiService;

    @Override
    public List<OpenAiProcessResponseDTO> process(final OpenAIRequestDTO request) {
        validator.validate(request);
        return List.of(openAiService.processExam(request));
    }
}

package com.examsolver.openai.service.processing;

import com.examsolver.openai.service.processing.strategy.ProcessingStrategy;
import com.examsolver.openai.service.processing.strategy.ProcessingStrategyResolver;
import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiServiceImpl implements OpenAiService {

    private final ProcessingStrategyResolver resolver;

    @Override
    public OpenAiProcessResponseDTO processExam(final OpenAIRequestDTO req) {
        final ProcessingStrategy strategy = resolver.resolve(req);
        return strategy.process(req);
    }
}

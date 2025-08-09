package com.examsolver.openai.facade;

import com.examsolver.openai.config.ModelSelector;
import com.examsolver.openai.dto.client.Message;
import com.examsolver.openai.dto.client.OpenAiRequest;
import com.examsolver.openai.dto.client.Role;
import com.examsolver.openai.service.processing.OpenAiService;
import com.examsolver.openai.service.prompt.PromptBuilderService;
import com.examsolver.openai.service.validation.RequestValidatorService;
import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import com.examsolver.shared.dtos.response.OpenAiProcessResponseDTO;
import com.examsolver.shared.enums.ExamMode;
import com.examsolver.shared.enums.FallbackReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class OpenAiProcessingFacadeImpl implements OpenAiProcessingFacade {

    private final RequestValidatorService validator;
    private final PromptBuilderService promptBuilder;
    private final ModelSelector modelSelector;
    private final OpenAiService openAiService;

    @Override
    public List<OpenAiProcessResponseDTO> process(OpenAIRequestDTO request) {
        // 1) Validate input
        validator.validate(request);

        final ExamMode mode = request.getMode();
        final String lang = request.getDetectedLanguage();
        final boolean fallback = Boolean.TRUE.equals(request.getFallbackRequired());
        final FallbackReasonCode reason =
                fallback ? FallbackReasonCode.valueOf(request.getFallbackCode()) : null;


        final List<OpenAiProcessResponseDTO> out = new ArrayList<>(request.getExercises().size());
        for (String exercise : request.getExercises()) {
            OpenAiProcessResponseDTO res = fallback
                    ? processVision(mode, lang, reason)       // prompt-only por ahora
                    : processText(mode, lang, exercise);      // ejercicio limpio
            out.add(res);
        }
        return out;
    }

    private OpenAiProcessResponseDTO processText(ExamMode mode, String detectedLanguage, String exercise) {
        final String prompt = promptBuilder.buildText(mode, detectedLanguage, exercise);
        final String model = modelSelector.text();

        final OpenAiRequest req = OpenAiRequest.builder()
                .model(model)
                .messages(List.of(new Message(Role.USER, prompt)))
                .build();

        return openAiService.processExam(req);
    }

    private OpenAiProcessResponseDTO processVision(ExamMode mode, String detectedLanguage, FallbackReasonCode reason) {
        final String prompt = promptBuilder.buildVision(mode, detectedLanguage, reason);
        final String model = modelSelector.vision();

        final OpenAiRequest req = OpenAiRequest.builder()
                .model(model)
                .messages(List.of(new Message(Role.USER, prompt)))
                .build();

        return openAiService.processExam(req);
    }

}

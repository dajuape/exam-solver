package com.examsolver.openai.service.validation;

import com.examsolver.openai.exception.OpenAiValidationException;
import com.examsolver.shared.dtos.request.OpenAIRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class RequestValidatorImplService implements RequestValidatorService {

    @Override
    public void validate(OpenAIRequestDTO request) {
        if (request == null) {
            throw new OpenAiValidationException("Request must not be null");
        }

        if (!StringUtils.hasText(request.getExamId())) {
            throw new OpenAiValidationException("examId must not be blank");
        }

        if (request.getMode() == null) {
            throw new OpenAiValidationException("mode must not be null");
        }

        if (!StringUtils.hasText(request.getDetectedLanguage())) {
            throw new OpenAiValidationException("detectedLanguage must not be blank");
        }

        List<String> exercises = request.getExercises();
        if (exercises == null || exercises.isEmpty()) {
            throw new OpenAiValidationException("exercises must not be null or empty");
        }

        if (request.getFallbackRequired() == null) {
            throw new OpenAiValidationException("fallbackRequired flag must not be null");
        }

        if (request.getFallbackRequired()) {
            if (!StringUtils.hasText(request.getBase64Image())) {
                throw new OpenAiValidationException("base64Image must be provided when fallbackRequired is true");
            }
            if (!StringUtils.hasText(request.getFallbackCode())) {
                throw new OpenAiValidationException("fallbackCode must be provided when fallbackRequired is true");
            }
        }

        if (!request.getFallbackRequired() && request.getExtractedText() == null) {
            log.warn("extractedText is null but fallbackRequired is false");
        }

        log.debug("OpenAIRequestDTO validated successfully for examId={}", request.getExamId());
    }
}

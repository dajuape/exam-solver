package com.examsolver.openai.service.prompt;

import com.examsolver.shared.enums.ExamMode;
import com.examsolver.shared.enums.FallbackReasonCode;

/**
 * Builds prompts for OpenAI models based on exam mode and input content.
 */
public interface PromptBuilderService {

    /**
     * Builds a prompt for text-based processing.
     *
     * @param mode             exam mode
     * @param detectedLanguage detected language of the exam
     * @param exercise         single exercise text (already cleaned and extracted)
     * @return prompt string ready for OpenAI text model
     */
    String buildText(ExamMode mode, String detectedLanguage, String exercise);

    /**
     * Builds a prompt for vision-based fallback processing.
     *
     * @param mode             exam mode
     * @param detectedLanguage detected language (if available)
     * @param fallbackCode     reason why text extraction failed
     * @return prompt string ready for OpenAI vision model
     */
    String buildVision(ExamMode mode, String detectedLanguage, FallbackReasonCode fallbackCode);
}

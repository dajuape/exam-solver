package com.examsolver.openai.service.prompt;

import com.examsolver.shared.enums.ExamMode;
import com.examsolver.shared.enums.FallbackReasonCode;
import org.springframework.lang.Nullable;

/**
 * Builds prompts for OpenAI models based on exam mode and input content.
 */
public interface PromptBuilderService {

    /**
     * Builds a prompt for text-based processing without additional hints.
     *
     * @param mode             exam mode
     * @param detectedLanguage detected language of the exam
     * @param exercise         single exercise text (already cleaned and extracted)
     * @return prompt string ready for OpenAI text model
     */
    String buildText(ExamMode mode, String detectedLanguage, String exercise);

    /**
     * Builds a prompt for text-based processing with optional fallback hints.
     * <p>
     * Example: if {@code fallbackCode == TEXT_TOO_NOISY}, the implementation should
     * enrich the prompt with guidance about possible OCR artifacts, while still using
     * the text-based pipeline (no Vision fallback triggered).
     * </p>
     *
     * @param mode             exam mode
     * @param detectedLanguage detected language of the exam
     * @param exercise         single exercise text (already cleaned and extracted)
     * @param fallbackCode     optional hint about the extraction process outcome;
     *                         may be {@code null}. Certain codes (e.g. TEXT_TOO_NOISY)
     *                         should not trigger Vision but instead add clarifying guidance
     *                         to the prompt.
     * @return prompt string ready for OpenAI text model
     */
    String buildText(ExamMode mode, String detectedLanguage, String exercise,
                     @Nullable FallbackReasonCode fallbackCode);

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

package com.examsolver.openai.service.prompt;

import com.examsolver.shared.enums.ExamMode;
import com.examsolver.shared.enums.FallbackReasonCode;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class PromptBuilderServiceImpl implements PromptBuilderService {

    private static final Set<String> SUPPORTED_LANGS = Set.of("es", "en", "fr", "de");

    @Override
    public String buildText(ExamMode mode, String detectedLanguage, String exercise) {
        return buildText(mode, detectedLanguage, exercise, null);
    }

    @Override
    public String buildText(ExamMode mode, String detectedLanguage, String exercise, FallbackReasonCode fallbackCode) {
        if (mode == null) throw new IllegalArgumentException("mode is null");
        if (exercise == null || exercise.isBlank()) throw new IllegalArgumentException("exercise is blank");

        final String lang = normalizeLang(detectedLanguage);
        final String action = verbFor(mode, lang);
        final String extraGuidance = textGuidanceFor(fallbackCode);

        return """
                You are an expert teacher. Work in %s.
                Task: %s the exam exercise with clear, numbered steps. Use LaTeX where useful.
                Keep it concise and end with a one-line final result.%s
                Exercise:
                <<<%s>>>
                """.formatted(lang, action, extraGuidance, exercise.strip());
    }

    @Override
    public String buildVision(ExamMode mode, String detectedLanguage, FallbackReasonCode fallbackCode) {
        if (mode == null) throw new IllegalArgumentException("mode is null");

        final String lang = normalizeLang(detectedLanguage);
        final String action = verbFor(mode, lang);
        final String reason = humanReason(fallbackCode);
        final String guidance = guidanceFor(fallbackCode);

        return """
                OCR/text extraction was unreliable; infer from the image. Work in %s.
                Task: %s each visible exercise with short, numbered steps. Use LaTeX where useful.
                If text is noisy, denoise by intent and be explicit about assumptions.
                Fallback reason: %s
                Guidance: %s
                """.formatted(lang, action, reason, guidance);
    }


    private String normalizeLang(String lang) {
        if (lang == null || lang.isBlank()) return "en";
        final String l = lang.trim().toLowerCase(Locale.ROOT);
        return SUPPORTED_LANGS.contains(l) ? l : "en";
    }

    private String verbFor(ExamMode mode, String lang) {
        final boolean correct = (mode == ExamMode.CORRECT);
        return switch (lang) {
            case "es" -> correct ? "corrige" : "resuelve";
            case "fr" -> correct ? "corrige" : "résous";
            case "de" -> correct ? "korrigiere" : "löse";
            default -> correct ? "correct" : "solve";
        };
    }

    private String humanReason(FallbackReasonCode code) {
        if (code == null) return "unspecified";
        return switch (code) {
            case OCR_PDF_EXTRACTION_FAILED -> "OCR failed on PDF pages";
            case OCR_IMAGE_EXTRACTION_FAILED -> "OCR failed on image(s)";
            case OCR_IMAGE_DECODE_FAILED -> "image data could not be decoded";
            case PDFBOX_EXTRACTION_FAILED -> "PDF text extraction (PDFBox) failed";
            case NOUGAT_UNAVAILABLE -> "scientific OCR (Nougat) unavailable";
            case TEXT_TOO_NOISY -> "extracted text is too noisy/garbled";
            case EMPTY_EXTRACTION_RESULT -> "extraction returned empty content";
        };
    }

    private String guidanceFor(FallbackReasonCode code) {
        if (code == null) return "Read the image and reconstruct the exercises robustly.";
        return switch (code) {
            case OCR_PDF_EXTRACTION_FAILED, PDFBOX_EXTRACTION_FAILED ->
                    "Segment by visual cues (numbers, bullets, boxes). Read formulas directly from the image.";
            case OCR_IMAGE_EXTRACTION_FAILED, OCR_IMAGE_DECODE_FAILED ->
                    "Assume low quality/rotation. Describe any ambiguities and proceed with best-guess interpretation.";
            case TEXT_TOO_NOISY ->
                    "Ignore artifacts; infer intended statements (variables, operators, units). State assumptions briefly.";
            case EMPTY_EXTRACTION_RESULT ->
                    "Treat as fresh: identify problem statements visually and transcribe key symbols before solving.";
            case NOUGAT_UNAVAILABLE ->
                    "Math-heavy content: transcribe formulas as LaTeX from the image, then proceed step-by-step.";
        };
    }

    private String textGuidanceFor(FallbackReasonCode code) {
        if (code == FallbackReasonCode.TEXT_TOO_NOISY) {
            return """
                    
                    Guidance: The extracted text may contain OCR noise. Clarify variables/symbols when ambiguous, \
                    reconstruct intended statements if needed, and briefly state any assumptions before solving.""";
        }
        return "";
    }
}

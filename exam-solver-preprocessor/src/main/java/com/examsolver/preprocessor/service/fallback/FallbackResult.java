package com.examsolver.preprocessor.service.fallback;

import com.examsolver.shared.enums.FallbackReasonCode;

public record FallbackResult(String text,
                             boolean usedNougatFallback,
                             boolean nougatFailed,
                             FallbackReasonCode fallbackCode) {

    public static FallbackResult success(String text) {
        return new FallbackResult(text, false, false, null);
    }

    public static FallbackResult nougat(String latexText) {
        return new FallbackResult(latexText, true, false, null);
    }

    public static FallbackResult failed(String rawText, FallbackReasonCode code) {
        return new FallbackResult(rawText, true, true, code);
    }
}

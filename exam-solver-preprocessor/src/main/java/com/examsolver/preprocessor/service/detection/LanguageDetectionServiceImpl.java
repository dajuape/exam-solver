package com.examsolver.preprocessor.service.detection;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class LanguageDetectionServiceImpl implements LanguageDetectionService {

    private final LanguageDetector languageDetector;

    public LanguageDetectionServiceImpl() throws IOException {
        final List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
        languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();
        log.info("Language detector initialized. Loaded {} language profiles.", languageProfiles.size());
    }

    @Override
    public String detectLanguage(String input) {

        if (input == null || input.isEmpty()) {
            log.warn("Input for language detection is null or empty. Returning 'unknown'.");
            return "unknown";
        }

        final Optional<LdLocale> lang = languageDetector.detect(input);
        if (lang.isPresent()) {
            log.info("Detected language: {}", lang.get().getLanguage());
            return lang.get().getLanguage();
        } else {
            log.warn("Language could not be detected for input.");
            return "unknown";
        }
    }
}


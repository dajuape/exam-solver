package com.examsolver.preprocessor.service.cleaner;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of {@link TextCleaningService} that applies a cleaning pipeline
 * to raw text from OCR or PDF extraction.
 *
 * <p>Cleaning steps include:
 * <ul>
 *   <li>Removing tabs and duplicate spaces</li>
 *   <li>Fixing hyphenated line breaks (e.g., "ecua-\nción")</li>
 *   <li>Joining lines that are likely part of the same sentence</li>
 *   <li>Removing page numbers and repeated short lines (headers/footers)</li>
 *   <li>Trimming and normalizing whitespace</li>
 *   <li>Injecting a delimiter before exercise blocks (e.g., "Ejercicio A1")</li>
 * </ul>
 * </p>
 */

@Service
@Slf4j
public class TextCleaningServiceImpl implements TextCleaningService {

    @Value("${preprocessor.cleaning.max-line-length}")
    private int maxLineLength;


    @Override
    public String clean(String input) {
        if (input == null) return null;
        String cleaned = input;

        // 1. Remove tab characters
        cleaned = cleaned.replace("\t", " ");

        // 2. Join words split with hyphen and newline (e.g., "ecua-\nción" => "ecuación")
        cleaned = cleaned.replaceAll("-\\n", "");

        // 3. Join lines when the next line starts with lowercase (prevents breaking sentences)
        cleaned = cleaned.replaceAll("([\\p{L}\\p{N},;])\\n([\\p{Ll}])", "$1 $2");

        // 4. Remove lines that only contain 1-4 digits (possible page numbers)
        cleaned = cleaned.replaceAll("(?m)^\\d{1,4}$", "");

        // 5. Remove repeated short lines (headers/footers) – universal, language-agnostic
        cleaned = removeRepeatedShortLines(cleaned);

        // 6. Replace multiple newlines with a single newline
        cleaned = cleaned.replaceAll("\\n{2,}", "\n");

        // 7. Replace multiple spaces with a single space
        cleaned = cleaned.replaceAll(" +", " ");

        // 8. Remove leading and trailing spaces from each line
        cleaned = cleaned.replaceAll("(?m)^\\s+|\\s+$", "");

        // 9. Final trim
        cleaned = cleaned.trim();

        return cleaned;
    }


    /**
     * Removes lines that are short and repeated more than once in the text.
     * Useful for cleaning headers/footers in any language.
     *
     * @param text The input text.
     * @return Cleaned text.
     */
    private String removeRepeatedShortLines(String text) {
        final String[] lines = text.split("\\n");
        final Map<String, Integer> counter = new HashMap<>();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() <= maxLineLength && !trimmed.isEmpty())
                counter.put(trimmed, counter.getOrDefault(trimmed, 0) + 1);
        }

       final Set<String> repeated = counter.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        return Arrays.stream(lines)
                .filter(line -> !repeated.contains(line.trim()))
                .collect(Collectors.joining("\n"));
    }
}
package com.examsolver.preprocessor.service.splitting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExerciseSplitterServiceImpl implements ExerciseSplitterService {

    @Override
    public List<String> split(String input, String delimiter) {
        if (input == null || delimiter == null || delimiter.isEmpty()) {
            log.warn("Input text or delimiter is null/empty. Returning empty list.");
            return List.of();
        }

        // Split and trim each exercise, filter out empty strings
        final List<String> exercises = Arrays.stream(input.split(java.util.regex.Pattern.quote(delimiter)))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        log.info("Split input into {} exercises using delimiter '{}'.", exercises.size(), delimiter);
        return exercises;
    }
}

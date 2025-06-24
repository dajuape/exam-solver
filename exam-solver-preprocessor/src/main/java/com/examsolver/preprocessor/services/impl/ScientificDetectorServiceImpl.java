package com.examsolver.preprocessor.services.impl;

import com.examsolver.preprocessor.services.ScientificDetectorService;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ScientificDetectorServiceImpl implements ScientificDetectorService {

    // Detecta símbolos o patrones matemáticos comunes
    private static final Pattern MATH_PATTERN = Pattern.compile(
            "(∫|lim|π|f\\(|\\^2|√|\\||\\{|\\}|\\(|\\)|\\d+\\s*[a-zA-Z]\\d*|\\=|\\\\begin|\\\\frac|→)"
    );

    @Override
    public boolean isScientific(String text) {
        return MATH_PATTERN.matcher(text).find();
    }
}

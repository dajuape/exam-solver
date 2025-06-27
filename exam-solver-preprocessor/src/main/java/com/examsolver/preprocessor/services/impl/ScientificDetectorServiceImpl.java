package com.examsolver.preprocessor.services.impl;

import com.examsolver.preprocessor.services.ScientificDetectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ScientificDetectorServiceImpl implements ScientificDetectorService {

    private static final Pattern MATH_PATTERN = Pattern.compile(
            "(∫|" +
                    "lim\\s+[a-zA-Z]+\\s*→|" +                       // lim x → ...
                    "\\\\frac\\{[^}]+}\\{[^}]+}|" +                  // \frac{a}{b}
                    "f\\([a-zA-Z]+\\)|" +                            // f(x), g(t)
                    "\\^\\d+|" +                                     // exponent: ^2, ^3
                    "\\b\\d+\\s*[a-zA-Z]\\b|" +                      // coefficient: 3x (word boundaries)
                    "[a-zA-Z]\\s*=\\s*\\d+|" +                       // equality: x = 5
                    "\\\\begin\\{equation})"                         // LaTeX equation
    );


    private static final int MIN_MATCHES = 3;

    @Override
    public boolean isScientific(String text) {
        Matcher matcher = MATH_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            log.debug("Scientific match: {}", matcher.group());
            count++;
            if (count >= MIN_MATCHES) return true;
        }
        return false;
    }
}

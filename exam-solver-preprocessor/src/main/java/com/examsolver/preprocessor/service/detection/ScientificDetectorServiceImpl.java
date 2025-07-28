package com.examsolver.preprocessor.service.detection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link ScientificDetectorService} that uses regex-based pattern
 * matching to detect mathematical or scientific expressions in the input text.
 *
 * <p>The detection includes elements like:
 * <ul>
 *   <li>LaTeX structures (e.g., \frac{}, \begin{equation})</li>
 *   <li>Mathematical symbols and functions (∫, f(x), lim x→...)</li>
 *   <li>Algebraic expressions (e.g., 3x, x = 5, x^2)</li>
 * </ul>
 *
 * <p>If a minimum number of matches is found, the text is considered scientific.</p>
 */
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


    @Value("${preprocessor.scientific.min-matches}")
    private int minMatches;

    @Override
    public boolean isScientific(String text) {
        final Matcher matcher = MATH_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            log.debug("Scientific match: {}", matcher.group());
            count++;
            if (count >= minMatches) return true;
        }
        return false;
    }
}

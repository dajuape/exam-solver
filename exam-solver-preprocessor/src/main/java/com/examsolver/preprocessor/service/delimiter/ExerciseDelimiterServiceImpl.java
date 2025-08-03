package com.examsolver.preprocessor.service.delimiter;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExerciseDelimiterServiceImpl implements ExerciseDelimiterService {

    @Override
    public String setDeilimter(String text, String delimiter) {
        final Pattern p = Pattern.compile("(?i)(\\*{0,2}\\s*(Ejercicio|Exercise|Exercice|Übung|Aufgabe)\\s+[A-Z]\\d\\s*\\*{0,2})");
        final Matcher m = p.matcher(text);
        final StringBuffer sb = new StringBuffer();

        while (m.find()) {
            final String match = m.group(1).trim();
            // Ensure line break before and after the block
            final String replacement = String.format("\n%s\n%s\n", delimiter, match);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString().stripLeading(); // remove leading \n if any
    }


}

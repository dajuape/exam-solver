package com.examsolver.preprocessor.service.strategy;

import com.examsolver.shared.enums.FileType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Resolves the appropriate {@link PreprocessStrategy} implementation
 * based on the detected or declared {@link FileType}.
 *
 * <p>Typically, {@link FileType#PDF} maps to {@link PdfPreprocessServiceImpl},
 * and {@link FileType#IMAGE} to {@link OcrPreprocessServiceImpl}.</p>
 *
 * <p>The resolution is based on instance type and is performed once
 * during bean initialization.</p>
 */
@Component
public class PreprocessStrategyResolver {

    private final Map<FileType, PreprocessStrategy> strategies;

    public PreprocessStrategyResolver(List<PreprocessStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(this::resolveKey, s -> s));
    }

    public PreprocessStrategy resolve(FileType fileType) {
        return strategies.get(fileType);
    }

    private FileType resolveKey(PreprocessStrategy strategy) {
        if (strategy instanceof PdfPreprocessServiceImpl) return FileType.PDF;
        if (strategy instanceof OcrPreprocessServiceImpl) return FileType.IMAGE;
        throw new IllegalArgumentException("Unknown strategy: " + strategy.getClass());
    }
}

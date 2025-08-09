package com.examsolver.shared.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiProcessResponseDTO {

    private UUID examId;
    private String modelUsed;
    private List<Result> results;

    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private int index;
        private String content;
        private String finishReason;
    }
}

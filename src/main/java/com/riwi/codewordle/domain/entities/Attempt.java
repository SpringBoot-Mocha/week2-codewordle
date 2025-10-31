package com.riwi.codewordle.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attempt {
    private Long id;
    private Long gameId;
    private String guessedWord;
    private String feedback;
    private Integer attemptNumber;
    private Boolean isCorrect;
    private LocalDateTime attemptedAt;
}

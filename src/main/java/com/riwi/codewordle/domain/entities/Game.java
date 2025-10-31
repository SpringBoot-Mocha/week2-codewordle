package com.riwi.codewordle.domain.entities;

import com.riwi.codewordle.domain.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    private Long id;
    private Long themeId;
    private Long wordId;
    private String targetWord;
    private Integer maxAttempts;
    private Integer currentAttempts;
    private GameStatus status;
    private Boolean won;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}

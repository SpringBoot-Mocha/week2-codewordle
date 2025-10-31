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
public class Theme {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}

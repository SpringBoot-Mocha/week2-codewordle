package com.crudactivity.codewordle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameModel {
    private int id_game;
    private String topic;
    private String state;
    private String hidden_word;
    private int attempts;

}

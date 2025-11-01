package com.crudactivity.codewordle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptModel {
private int id_attempt;
private int id_game;
private String attempt;
private String result;

}

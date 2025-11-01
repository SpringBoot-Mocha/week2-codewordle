package com.crudactivity.codewordle.service;

import com.crudactivity.codewordle.model.GameModel;
import java.util.Optional;

public interface GameService {
    GameModel startNewGame(String topic);
    Optional<GameModel> getGame(int id_game);
    void changeState(int id_game, String state);
    void incrementAttempts(int id_game);
    String evaluateAttempt(String attempt, String target);
}

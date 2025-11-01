package com.crudactivity.codewordle.repository;

import com.crudactivity.codewordle.model.GameModel;

import java.util.Optional;

public interface GameRepository {
    public GameModel save(GameModel gameModel);
    public void changeState(int id_game, String state);
    public Optional<GameModel> getActualGame(int id_game);
}

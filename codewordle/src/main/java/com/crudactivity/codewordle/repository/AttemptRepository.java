package com.crudactivity.codewordle.repository;

import com.crudactivity.codewordle.model.AttemptModel;

import java.util.List;

public interface AttemptRepository {
    public void save(AttemptModel attempt);
    public List<AttemptModel> findByGame(int id_game);
}

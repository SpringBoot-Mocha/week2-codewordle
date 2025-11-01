package com.crudactivity.codewordle.service;

import com.crudactivity.codewordle.model.AttemptModel;
import java.util.List;

public interface AttemptService {
    void saveAttempt(AttemptModel attempt);
    List<AttemptModel> getAttemptsByGame(int id_game);
}

package com.crudactivity.codewordle.service.impl;

import com.crudactivity.codewordle.model.AttemptModel;
import com.crudactivity.codewordle.repository.AttemptRepository;
import com.crudactivity.codewordle.service.AttemptService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepository attemptRepository;

    public AttemptServiceImpl(AttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @Override
    public void saveAttempt(AttemptModel attempt) {
        attemptRepository.save(attempt);
    }

    @Override
    public List<AttemptModel> getAttemptsByGame(int id_game) {
        return attemptRepository.findByGame(id_game);
    }
}

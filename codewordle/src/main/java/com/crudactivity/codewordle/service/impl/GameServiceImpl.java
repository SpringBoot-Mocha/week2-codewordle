package com.crudactivity.codewordle.service.impl;

import com.crudactivity.codewordle.model.GameModel;
import com.crudactivity.codewordle.repository.AttemptRepository;
import com.crudactivity.codewordle.repository.GameIMP;
import com.crudactivity.codewordle.repository.GameRepository;
import com.crudactivity.codewordle.repository.WordRepository;
import com.crudactivity.codewordle.service.GameService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    private final WordRepository wordRepository;
    private final GameRepository gameRepository;
    private final AttemptRepository attemptRepository;

    public GameServiceImpl(WordRepository wordRepository,
                           GameRepository gameRepository,
                           AttemptRepository attemptRepository) {
        this.wordRepository = wordRepository;
        this.gameRepository = gameRepository;
        this.attemptRepository = attemptRepository;
    }

    @Override
    public GameModel startNewGame(String topic) {
        String target = wordRepository.findRandomWord(topic)
                .orElseThrow(() -> new RuntimeException("No hay palabras disponibles para el tema: " + topic));
        GameModel game = new GameModel();
        game.setTopic(topic);
        game.setState("IN_PROGRESS");
        game.setHidden_word(target);
        game.setAttempts(0);
        // save via repository
        return gameRepository.save(game);
    }

    @Override
    public Optional<GameModel> getGame(int id_game) {
        return gameRepository.getActualGame(id_game);
    }

    @Override
    public void changeState(int id_game, String state) {
        gameRepository.changeState(id_game, state);
    }

    @Override
    public void incrementAttempts(int id_game) {
        // If your GameIMP has helper increment, try to cast to GameIMP to call it
        if (gameRepository instanceof GameIMP) {
            ((GameIMP) gameRepository).incrementAttempts(id_game);
        } else {
            // fallback: update attempts by reading and updating
            Optional<GameModel> g = gameRepository.getActualGame(id_game);
            g.ifPresent(game -> {
                game.setAttempts(game.getAttempts() + 1);
                ((GameIMP) gameRepository).updateAttempts(id_game, game.getAttempts());
            });
        }
    }

    /**
     * Evaluates an attempt vs target using Wordle rules:
     * - Mark greens first (correct letter+position)
     * - Then mark yellows only for remaining unmatched letters considering counts
     * Returns a string of emojis same length as attempt: 🟩 🟨 ⬜
     */
    @Override
    public String evaluateAttempt(String attempt, String target) {
        String a = attempt.toUpperCase(Locale.ROOT);
        String t = target.toUpperCase(Locale.ROOT);

        int n = Math.min(a.length(), t.length());
        char[] res = new char[n]; // placeholders
        // use arrays to mark matched positions
        boolean[] matchedTarget = new boolean[t.length()];
        boolean[] matchedAttempt = new boolean[a.length()];

        // first pass: greens
        for (int i = 0; i < n; i++) {
            if (a.charAt(i) == t.charAt(i)) {
                res[i] = 'G'; // green
                matchedTarget[i] = true;
                matchedAttempt[i] = true;
            }
        }

        // second pass: yellows for remaining letters (respect counts)
        for (int i = 0; i < n; i++) {
            if (matchedAttempt[i]) continue;
            char ca = a.charAt(i);
            boolean found = false;
            for (int j = 0; j < t.length(); j++) {
                if (!matchedTarget[j] && ca == t.charAt(j)) {
                    found = true;
                    matchedTarget[j] = true;
                    break;
                }
            }
            res[i] = found ? 'Y' : 'B'; // Y = yellow, B = blank/gray
        }

        // build emoji string
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char c = res[i];
            if (c == 'G') out.append("🟩");
            else if (c == 'Y') out.append("🟨");
            else out.append("⬜");
        }
        return out.toString();
    }
}

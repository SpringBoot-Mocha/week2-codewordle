package com.riwi.codewordle.repository;

import com.riwi.codewordle.domain.entities.Game;
import com.riwi.codewordle.domain.enums.GameStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Game> gameRowMapper = (ResultSet, rowNum) -> Game.builder()
            .id(ResultSet.getLong("id"))
            .themeId(ResultSet.getLong("theme_id"))
            .wordId(ResultSet.getLong("word_id"))
            .targetWord(ResultSet.getString("target_word"))
            .maxAttempts(ResultSet.getInt("max_attempts"))
            .currentAttempts(ResultSet.getInt("current_attempts"))
            .status(GameStatus.valueOf(ResultSet.getString("status")))
            .won(ResultSet.getBoolean("won"))
            .startedAt(ResultSet.getTimestamp("started_at") != null ? 
                ResultSet.getTimestamp("started_at").toLocalDateTime() : null)
            .finishedAt(ResultSet.getTimestamp("finished_at") != null ? 
                ResultSet.getTimestamp("finished_at").toLocalDateTime() : null)
            .build();

    public Optional<Game> findById(Long id) {
        String sql = "SELECT * FROM games WHERE id = ?";
        List<Game> games = jdbcTemplate.query(sql, gameRowMapper, id);
        return games.isEmpty() ? Optional.empty() : Optional.of(games.get(0));
    }

    public List<Game> findAll() {
        String sql = "SELECT * FROM games";
        return jdbcTemplate.query(sql, gameRowMapper);
    }

    public List<Game> findByThemeId(Long themeId) {
        String sql = "SELECT * FROM games WHERE theme_id = ?";
        return jdbcTemplate.query(sql, gameRowMapper, themeId);
    }

    public List<Game> findByStatus(GameStatus status) {
        String sql = "SELECT * FROM games WHERE status = ?";
        return jdbcTemplate.query(sql, gameRowMapper, status.toString());
    }

    public Game save(Game game) {
        if (game.getId() == null) {
            return create(game);
        } else {
            return update(game);
        }
    }

    private Game create(Game game) {
        String sql = "INSERT INTO games (theme_id, word_id, target_word, max_attempts, current_attempts, status, won) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
                game.getThemeId(),
                game.getWordId(),
                game.getTargetWord(),
                game.getMaxAttempts(),
                game.getCurrentAttempts(),
                game.getStatus().toString(),
                game.getWon());
        
        return findByTargetWord(game.getTargetWord()).orElse(game);
    }

    private Game update(Game game) {
        String sql = "UPDATE games SET theme_id = ?, word_id = ?, target_word = ?, max_attempts = ?, " +
                "current_attempts = ?, status = ?, won = ?, finished_at = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
                game.getThemeId(),
                game.getWordId(),
                game.getTargetWord(),
                game.getMaxAttempts(),
                game.getCurrentAttempts(),
                game.getStatus().toString(),
                game.getWon(),
                game.getFinishedAt() != null ? Timestamp.valueOf(game.getFinishedAt()) : null,
                game.getId());
        
        return findById(game.getId()).orElse(game);
    }

    private Optional<Game> findByTargetWord(String targetWord) {
        String sql = "SELECT * FROM games WHERE target_word = ? ORDER BY id DESC LIMIT 1";
        List<Game> games = jdbcTemplate.query(sql, gameRowMapper, targetWord);
        return games.isEmpty() ? Optional.empty() : Optional.of(games.get(0));
    }

    public void delete(Long id) {
        String sql = "DELETE FROM games WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

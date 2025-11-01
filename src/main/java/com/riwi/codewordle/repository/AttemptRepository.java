package com.riwi.codewordle.repository;

import com.riwi.codewordle.domain.entities.Attempt;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AttemptRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Attempt> attemptRowMapper = (ResultSet, rowNum) -> Attempt.builder()
            .id(ResultSet.getLong("id"))
            .gameId(ResultSet.getLong("game_id"))
            .guessedWord(ResultSet.getString("guessed_word"))
            .feedback(ResultSet.getString("feedback"))
            .attemptNumber(ResultSet.getInt("attempt_number"))
            .isCorrect(ResultSet.getBoolean("is_correct"))
            .attemptedAt(ResultSet.getTimestamp("attempted_at") != null ? 
                ResultSet.getTimestamp("attempted_at").toLocalDateTime() : null)
            .build();

    public Optional<Attempt> findById(Long id) {
        String sql = "SELECT * FROM attempts WHERE id = ?";
        List<Attempt> attempts = jdbcTemplate.query(sql, attemptRowMapper, id);
        return attempts.isEmpty() ? Optional.empty() : Optional.of(attempts.get(0));
    }

    public List<Attempt> findByGameId(Long gameId) {
        String sql = "SELECT * FROM attempts WHERE game_id = ? ORDER BY attempt_number ASC";
        return jdbcTemplate.query(sql, attemptRowMapper, gameId);
    }

    public List<Attempt> findAll() {
        String sql = "SELECT * FROM attempts";
        return jdbcTemplate.query(sql, attemptRowMapper);
    }

    public Attempt save(Attempt attempt) {
        if (attempt.getId() == null) {
            return create(attempt);
        } else {
            return update(attempt);
        }
    }

    private Attempt create(Attempt attempt) {
        String sql = "INSERT INTO attempts (game_id, guessed_word, feedback, attempt_number, is_correct) " +
                "VALUES (?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql,
                attempt.getGameId(),
                attempt.getGuessedWord(),
                attempt.getFeedback(),
                attempt.getAttemptNumber(),
                attempt.getIsCorrect());
        
        return findLatestAttemptByGameId(attempt.getGameId()).orElse(attempt);
    }

    private Attempt update(Attempt attempt) {
        String sql = "UPDATE attempts SET game_id = ?, guessed_word = ?, feedback = ?, " +
                "attempt_number = ?, is_correct = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
                attempt.getGameId(),
                attempt.getGuessedWord(),
                attempt.getFeedback(),
                attempt.getAttemptNumber(),
                attempt.getIsCorrect(),
                attempt.getId());
        
        return findById(attempt.getId()).orElse(attempt);
    }

    private Optional<Attempt> findLatestAttemptByGameId(Long gameId) {
        String sql = "SELECT * FROM attempts WHERE game_id = ? ORDER BY id DESC LIMIT 1";
        List<Attempt> attempts = jdbcTemplate.query(sql, attemptRowMapper, gameId);
        return attempts.isEmpty() ? Optional.empty() : Optional.of(attempts.get(0));
    }

    public void delete(Long id) {
        String sql = "DELETE FROM attempts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Integer countByGameId(Long gameId) {
        String sql = "SELECT COUNT(*) FROM attempts WHERE game_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, gameId);
    }
}

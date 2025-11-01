package com.riwi.codewordle.repository;

import com.riwi.codewordle.domain.entities.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WordRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Word> wordRowMapper = (ResultSet, rowNum) -> Word.builder()
            .id(ResultSet.getLong("id"))
            .word(ResultSet.getString("word"))
            .themeId(ResultSet.getLong("theme_id"))
            .hint(ResultSet.getString("hint"))
            .createdAt(ResultSet.getTimestamp("created_at") != null ? 
                ResultSet.getTimestamp("created_at").toLocalDateTime() : null)
            .build();

    public Optional<Word> findById(Long id) {
        String sql = "SELECT * FROM words WHERE id = ?";
        List<Word> words = jdbcTemplate.query(sql, wordRowMapper, id);
        return words.isEmpty() ? Optional.empty() : Optional.of(words.get(0));
    }

    public List<Word> findByThemeId(Long themeId) {
        String sql = "SELECT * FROM words WHERE theme_id = ?";
        return jdbcTemplate.query(sql, wordRowMapper, themeId);
    }

    public Optional<Word> findByWordAndThemeId(String word, Long themeId) {
        String sql = "SELECT * FROM words WHERE word = ? AND theme_id = ?";
        List<Word> words = jdbcTemplate.query(sql, wordRowMapper, word, themeId);
        return words.isEmpty() ? Optional.empty() : Optional.of(words.get(0));
    }

    public Optional<Word> findRandomWordByThemeId(Long themeId) {
        String sql = "SELECT * FROM words WHERE theme_id = ? ORDER BY RAND() LIMIT 1";
        List<Word> words = jdbcTemplate.query(sql, wordRowMapper, themeId);
        return words.isEmpty() ? Optional.empty() : Optional.of(words.get(0));
    }

    public List<Word> findAll() {
        String sql = "SELECT * FROM words";
        return jdbcTemplate.query(sql, wordRowMapper);
    }

    public Word save(Word word) {
        if (word.getId() == null) {
            return create(word);
        } else {
            return update(word);
        }
    }

    private Word create(Word word) {
        String sql = "INSERT INTO words (word, theme_id, hint) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, word.getWord(), word.getThemeId(), word.getHint());
        
        return findByWordAndThemeId(word.getWord(), word.getThemeId()).orElse(word);
    }

    private Word update(Word word) {
        String sql = "UPDATE words SET word = ?, theme_id = ?, hint = ? WHERE id = ?";
        jdbcTemplate.update(sql, word.getWord(), word.getThemeId(), word.getHint(), word.getId());
        
        return findById(word.getId()).orElse(word);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM words WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

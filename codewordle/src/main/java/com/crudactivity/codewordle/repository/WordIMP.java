package com.crudactivity.codewordle.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class WordIMP implements WordRepository {

    private final JdbcTemplate jdbc;

    public WordIMP(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<String> findByTopic(String topic) {
        String sql = "SELECT word FROM words WHERE topic = ?";
        return jdbc.query(sql, (rs, i) -> rs.getString("word"), topic);
    }

    @Override
    public Optional<String> findRandomWord(String topic) {
        List<String> words = findByTopic(topic);
        if (words == null || words.isEmpty()) return Optional.empty();
        Collections.shuffle(words);
        return Optional.of(words.get(0));
    }
}

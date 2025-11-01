package com.crudactivity.codewordle.repository;

import com.crudactivity.codewordle.model.AttemptModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class AttemptIMP implements AttemptRepository {

    private final JdbcTemplate jdbc;

    public AttemptIMP(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(AttemptModel attempt) {
        String sql = "INSERT INTO attempt (id_game, attempt, result) VALUES (?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, attempt.getId_game());
            ps.setString(2, attempt.getAttempt());
            ps.setString(3, attempt.getResult());
            return ps;
        }, kh);
        // no devuelve id pero si quieres lo puedes recuperar con kh.getKey()
    }

    @Override
    public List<AttemptModel> findByGame(int id_game) {
        String sql = "SELECT * FROM attempt WHERE id_game = ? ORDER BY id_attempt ASC";
        return jdbc.query(sql, (rs, i) -> new AttemptModel(
                rs.getInt("id_attempt"),
                rs.getInt("id_game"),
                rs.getString("attempt"),
                rs.getString("result")
        ), id_game);
    }
}

package com.crudactivity.codewordle.repository;

import com.crudactivity.codewordle.model.GameModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class GameIMP implements GameRepository {

    private final JdbcTemplate jdbc;

    public GameIMP(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public GameModel save(GameModel gameModel) {
        String sql = "INSERT INTO game (topic, state, hidden_word, attempts) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, gameModel.getTopic());
            ps.setString(2, gameModel.getState());
            ps.setString(3, gameModel.getHidden_word());
            ps.setInt(4, gameModel.getAttempts());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            gameModel.setId_game(key.intValue());
        }
        return gameModel;
    }

    @Override
    public void changeState(int id_game, String state) {
        String sql = "UPDATE game SET state = ? WHERE id_game = ?";
        jdbc.update(sql, state, id_game);
    }

    @Override
    public Optional<GameModel> getActualGame(int id_game) {
        String sql = "SELECT * FROM game WHERE id_game = ?";
        return jdbc.query(sql, (rs, rowNum) -> new GameModel(
                rs.getInt("id_game"),
                rs.getString("topic"),
                rs.getString("state"),
                rs.getString("hidden_word"),
                rs.getInt("attempts")
        ), id_game).stream().findFirst();
    }

    // Agrego helper para actualizar attempts (opcional - se usa desde el service)
    public void incrementAttempts(int id_game) {
        String sql = "UPDATE game SET attempts = attempts + 1 WHERE id_game = ?";
        jdbc.update(sql, id_game);
    }

    public void updateAttempts(int id_game, int attempts) {
        String sql = "UPDATE game SET attempts = ? WHERE id_game = ?";
        jdbc.update(sql, attempts, id_game);
    }
}

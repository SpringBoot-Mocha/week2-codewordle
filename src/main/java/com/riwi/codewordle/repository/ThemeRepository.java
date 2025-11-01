package com.riwi.codewordle.repository;

import com.riwi.codewordle.domain.entities.Theme;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ThemeRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Theme> themeRowMapper = (ResultSet, rowNum) -> Theme.builder()
            .id(ResultSet.getLong("id"))
            .name(ResultSet.getString("name"))
            .description(ResultSet.getString("description"))
            .createdAt(ResultSet.getTimestamp("created_at") != null ? 
                ResultSet.getTimestamp("created_at").toLocalDateTime() : null)
            .build();

    public Optional<Theme> findById(Long id) {
        String sql = "SELECT * FROM themes WHERE id = ?";
        List<Theme> themes = jdbcTemplate.query(sql, themeRowMapper, id);
        return themes.isEmpty() ? Optional.empty() : Optional.of(themes.get(0));
    }

    public List<Theme> findAll() {
        String sql = "SELECT * FROM themes";
        return jdbcTemplate.query(sql, themeRowMapper);
    }

    public Optional<Theme> findByName(String name) {
        String sql = "SELECT * FROM themes WHERE name = ?";
        List<Theme> themes = jdbcTemplate.query(sql, themeRowMapper, name);
        return themes.isEmpty() ? Optional.empty() : Optional.of(themes.get(0));
    }

    public Theme save(Theme theme) {
        if (theme.getId() == null) {
            return create(theme);
        } else {
            return update(theme);
        }
    }

    private Theme create(Theme theme) {
        String sql = "INSERT INTO themes (name, description) VALUES (?, ?)";
        jdbcTemplate.update(sql, theme.getName(), theme.getDescription());
        
        return findByName(theme.getName()).orElse(theme);
    }

    private Theme update(Theme theme) {
        String sql = "UPDATE themes SET name = ?, description = ? WHERE id = ?";
        jdbcTemplate.update(sql, theme.getName(), theme.getDescription(), theme.getId());
        
        return findById(theme.getId()).orElse(theme);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM themes WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

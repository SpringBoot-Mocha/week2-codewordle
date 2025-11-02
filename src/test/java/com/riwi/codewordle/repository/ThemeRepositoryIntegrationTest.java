package com.riwi.codewordle.repository;

import com.riwi.codewordle.domain.entities.Theme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ThemeRepository
 * Tests the repository layer with actual H2 database connection
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class ThemeRepositoryIntegrationTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Clean up before each test - delete in correct order due to FK constraints
        jdbcTemplate.execute("DELETE FROM words");
        jdbcTemplate.execute("DELETE FROM themes");
    }

    @Test
    void testSaveTheme_Success() {
        // Arrange
        Theme theme = Theme.builder()
                .name("JAVA")
                .description("Java Programming Terms")
                .build();

        // Act
        Theme savedTheme = themeRepository.save(theme);

        // Assert
        assertNotNull(savedTheme.getId());
        assertEquals("JAVA", savedTheme.getName());
        assertEquals("Java Programming Terms", savedTheme.getDescription());
        assertNotNull(savedTheme.getCreatedAt());
    }

    @Test
    void testFindThemeById_Success() {
        // Arrange
        Theme theme = Theme.builder()
                .name("SPRING")
                .description("Spring Framework Terms")
                .build();
        Theme savedTheme = themeRepository.save(theme);

        // Act
        Optional<Theme> foundTheme = themeRepository.findById(savedTheme.getId());

        // Assert
        assertTrue(foundTheme.isPresent());
        assertEquals("SPRING", foundTheme.get().getName());
        assertEquals("Spring Framework Terms", foundTheme.get().getDescription());
    }

    @Test
    void testFindThemeById_NotFound() {
        // Act
        Optional<Theme> foundTheme = themeRepository.findById(999L);

        // Assert
        assertTrue(foundTheme.isEmpty());
    }

    @Test
    void testFindThemeByName_Success() {
        // Arrange
        Theme theme = Theme.builder()
                .name("DEVOPS")
                .description("DevOps Terms")
                .build();
        themeRepository.save(theme);

        // Act
        Optional<Theme> foundTheme = themeRepository.findByName("DEVOPS");

        // Assert
        assertTrue(foundTheme.isPresent());
        assertEquals("DEVOPS", foundTheme.get().getName());
    }

    @Test
    void testFindThemeByName_NotFound() {
        // Act
        Optional<Theme> foundTheme = themeRepository.findByName("NONEXISTENT");

        // Assert
        assertTrue(foundTheme.isEmpty());
    }

    @Test
    void testFindAllThemes() {
        // Arrange
        Theme theme1 = Theme.builder().name("JAVA").description("Java Terms").build();
        Theme theme2 = Theme.builder().name("SPRING").description("Spring Terms").build();
        Theme theme3 = Theme.builder().name("DATABASE").description("Database Terms").build();

        themeRepository.save(theme1);
        themeRepository.save(theme2);
        themeRepository.save(theme3);

        // Act
        List<Theme> allThemes = themeRepository.findAll();

        // Assert
        assertEquals(3, allThemes.size());
        assertTrue(allThemes.stream().anyMatch(t -> t.getName().equals("JAVA")));
        assertTrue(allThemes.stream().anyMatch(t -> t.getName().equals("SPRING")));
        assertTrue(allThemes.stream().anyMatch(t -> t.getName().equals("DATABASE")));
    }

    @Test
    void testUpdateTheme() {
        // Arrange
        Theme theme = Theme.builder()
                .name("KUBERNETES")
                .description("Old description")
                .build();
        Theme savedTheme = themeRepository.save(theme);

        savedTheme.setDescription("Updated Kubernetes description");

        // Act
        themeRepository.save(savedTheme);
        Optional<Theme> updatedTheme = themeRepository.findById(savedTheme.getId());

        // Assert
        assertTrue(updatedTheme.isPresent());
        assertEquals("Updated Kubernetes description", updatedTheme.get().getDescription());
    }

    @Test
    void testDeleteTheme() {
        // Arrange
        Theme theme = Theme.builder()
                .name("DOCKER")
                .description("Docker Terms")
                .build();
        Theme savedTheme = themeRepository.save(theme);

        // Act
        themeRepository.delete(savedTheme.getId());
        Optional<Theme> deletedTheme = themeRepository.findById(savedTheme.getId());

        // Assert
        assertTrue(deletedTheme.isEmpty());
    }

    @Test
    void testThemeUniqueName_Constraint() {
        // Arrange
        Theme theme1 = Theme.builder().name("UNIQUE_THEME").description("First theme").build();
        themeRepository.save(theme1);

        Theme theme2 = Theme.builder().name("UNIQUE_THEME").description("Second theme").build();

        // Act & Assert
        assertThrows(Exception.class, () -> themeRepository.save(theme2),
                "Should throw exception for duplicate theme name");
    }
}

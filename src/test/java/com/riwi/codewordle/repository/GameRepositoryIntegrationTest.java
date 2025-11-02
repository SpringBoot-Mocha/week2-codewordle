package com.riwi.codewordle.repository;

import com.riwi.codewordle.domain.entities.Game;
import com.riwi.codewordle.domain.entities.Word;
import com.riwi.codewordle.domain.enums.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for GameRepository
 * Tests the repository layer with actual H2 database connection
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class GameRepositoryIntegrationTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Word testWord;
    private Game testGame;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        jdbcTemplate.execute("DELETE FROM attempts");
        jdbcTemplate.execute("DELETE FROM games");
        jdbcTemplate.execute("DELETE FROM words");
        jdbcTemplate.execute("DELETE FROM themes");

        // Insert test data
        jdbcTemplate.execute("INSERT INTO themes (id, name, description) VALUES (1, 'JAVA', 'Java Terms')");
        jdbcTemplate.execute("INSERT INTO words (id, word, theme_id, hint) VALUES (1, 'CLASS', 1, 'OOP concept')");

        // Create test word
        testWord = Word.builder()
                .id(1L)
                .word("CLASS")
                .themeId(1L)
                .hint("OOP concept")
                .createdAt(LocalDateTime.now())
                .build();

        // Create test game
        testGame = Game.builder()
                .themeId(1L)
                .wordId(1L)
                .targetWord("CLASS")
                .maxAttempts(6)
                .currentAttempts(0)
                .status(GameStatus.IN_PROGRESS)
                .won(false)
                .startedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testSaveGame_Success() {
        // Act
        Game savedGame = gameRepository.save(testGame);

        // Assert
        assertNotNull(savedGame.getId());
        assertEquals("CLASS", savedGame.getTargetWord());
        assertEquals(GameStatus.IN_PROGRESS, savedGame.getStatus());
        assertFalse(savedGame.getWon());
    }

    @Test
    void testFindGameById_Success() {
        // Arrange
        Game savedGame = gameRepository.save(testGame);

        // Act
        Optional<Game> foundGame = gameRepository.findById(savedGame.getId());

        // Assert
        assertTrue(foundGame.isPresent());
        assertEquals("CLASS", foundGame.get().getTargetWord());
        assertEquals(GameStatus.IN_PROGRESS, foundGame.get().getStatus());
    }

    @Test
    void testFindGameById_NotFound() {
        // Act
        Optional<Game> foundGame = gameRepository.findById(999L);

        // Assert
        assertTrue(foundGame.isEmpty());
    }

    @Test
    void testUpdateGame_Status() {
        // Arrange
        Game savedGame = gameRepository.save(testGame);
        savedGame.setStatus(GameStatus.WON);
        savedGame.setWon(true);
        savedGame.setCurrentAttempts(3);

        // Act
        gameRepository.save(savedGame);
        Optional<Game> updatedGame = gameRepository.findById(savedGame.getId());

        // Assert
        assertTrue(updatedGame.isPresent());
        assertEquals(GameStatus.WON, updatedGame.get().getStatus());
        assertTrue(updatedGame.get().getWon());
        assertEquals(3, updatedGame.get().getCurrentAttempts());
    }

    @Test
    void testFindGamesByStatus_InProgress() {
        // Arrange
        gameRepository.save(testGame);

        // Create a finished game
        Game finishedGame = Game.builder()
                .themeId(1L)
                .wordId(1L)
                .targetWord("CLASS")
                .maxAttempts(6)
                .currentAttempts(6)
                .status(GameStatus.LOST)
                .won(false)
                .startedAt(LocalDateTime.now())
                .build();
        gameRepository.save(finishedGame);

        // Act
        List<Game> inProgressGames = gameRepository.findByStatus(GameStatus.IN_PROGRESS);

        // Assert
        assertEquals(1, inProgressGames.size());
        assertEquals(GameStatus.IN_PROGRESS, inProgressGames.get(0).getStatus());
    }

    @Test
    void testFindAllGames() {
        // Arrange
        gameRepository.save(testGame);
        gameRepository.save(Game.builder()
                .themeId(1L)
                .wordId(1L)
                .targetWord("ARRAY")
                .maxAttempts(6)
                .currentAttempts(0)
                .status(GameStatus.IN_PROGRESS)
                .won(false)
                .startedAt(LocalDateTime.now())
                .build());

        // Act
        List<Game> allGames = gameRepository.findAll();

        // Assert
        assertEquals(2, allGames.size());
    }

    @Test
    void testDeleteGame() {
        // Arrange
        Game savedGame = gameRepository.save(testGame);

        // Act
        gameRepository.delete(savedGame.getId());
        Optional<Game> deletedGame = gameRepository.findById(savedGame.getId());

        // Assert
        assertTrue(deletedGame.isEmpty());
    }
}

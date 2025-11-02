package com.riwi.codewordle.repository;

import com.riwi.codewordle.domain.entities.Attempt;
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
 * Integration tests for AttemptRepository
 * Tests the repository layer with actual H2 database connection
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class AttemptRepositoryIntegrationTest {

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long testGameId;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        jdbcTemplate.execute("DELETE FROM attempts");
        jdbcTemplate.execute("DELETE FROM games");
        jdbcTemplate.execute("DELETE FROM words");
        jdbcTemplate.execute("DELETE FROM themes");

        // Insert test data
        jdbcTemplate.execute("INSERT INTO themes (id, name, description) VALUES (1, 'JAVA', 'Java Terms')");
        jdbcTemplate.execute("INSERT INTO words (id, word, theme_id, hint) VALUES (1, 'CLASS', 1, 'OOP')");

        // Create a test game
        jdbcTemplate.execute("INSERT INTO games (id, theme_id, word_id, target_word, max_attempts, current_attempts, status, won) " +
                "VALUES (1, 1, 1, 'CLASS', 6, 0, 'IN_PROGRESS', false)");
        testGameId = 1L;
    }

    @Test
    void testSaveAttempt_Success() {
        // Arrange
        Attempt attempt = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("JAVA")
                .feedback("0100")
                .attemptNumber(1)
                .isCorrect(false)
                .build();

        // Act
        Attempt savedAttempt = attemptRepository.save(attempt);

        // Assert
        assertNotNull(savedAttempt.getId());
        assertEquals("JAVA", savedAttempt.getGuessedWord());
        assertEquals("0100", savedAttempt.getFeedback());
        assertEquals(1, savedAttempt.getAttemptNumber());
        assertFalse(savedAttempt.getIsCorrect());
        assertNotNull(savedAttempt.getAttemptedAt());
    }

    @Test
    void testFindAttemptById_Success() {
        // Arrange
        Attempt attempt = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("ARRAY")
                .feedback("0010")
                .attemptNumber(1)
                .isCorrect(false)
                .build();
        Attempt savedAttempt = attemptRepository.save(attempt);

        // Act
        Optional<Attempt> foundAttempt = attemptRepository.findById(savedAttempt.getId());

        // Assert
        assertTrue(foundAttempt.isPresent());
        assertEquals("ARRAY", foundAttempt.get().getGuessedWord());
        assertEquals("0010", foundAttempt.get().getFeedback());
    }

    @Test
    void testFindAttemptById_NotFound() {
        // Act
        Optional<Attempt> foundAttempt = attemptRepository.findById(999L);

        // Assert
        assertTrue(foundAttempt.isEmpty());
    }

    @Test
    void testCountByGameId() {
        // Arrange
        Attempt attempt1 = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("JAVA")
                .feedback("0100")
                .attemptNumber(1)
                .isCorrect(false)
                .build();
        Attempt attempt2 = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("ARRAY")
                .feedback("0010")
                .attemptNumber(2)
                .isCorrect(false)
                .build();
        Attempt attempt3 = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("CLASS")
                .feedback("2222")
                .attemptNumber(3)
                .isCorrect(true)
                .build();

        attemptRepository.save(attempt1);
        attemptRepository.save(attempt2);
        attemptRepository.save(attempt3);

        // Act
        int count = attemptRepository.countByGameId(testGameId);

        // Assert
        assertEquals(3, count);
    }

    @Test
    void testCountByGameId_EmptyGame() {
        // Act
        int count = attemptRepository.countByGameId(testGameId);

        // Assert
        assertEquals(0, count);
    }

    @Test
    void testFindAllAttempts() {
        // Arrange
        Attempt attempt1 = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("JAVA")
                .feedback("0100")
                .attemptNumber(1)
                .isCorrect(false)
                .build();
        Attempt attempt2 = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("ARRAY")
                .feedback("0010")
                .attemptNumber(2)
                .isCorrect(false)
                .build();

        attemptRepository.save(attempt1);
        attemptRepository.save(attempt2);

        // Act
        List<Attempt> allAttempts = attemptRepository.findAll();

        // Assert
        assertEquals(2, allAttempts.size());
    }

    @Test
    void testUpdateAttempt() {
        // Arrange
        Attempt attempt = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("LAMBDA")
                .feedback("0000")
                .attemptNumber(1)
                .isCorrect(false)
                .build();
        Attempt savedAttempt = attemptRepository.save(attempt);

        savedAttempt.setFeedback("1010");
        savedAttempt.setIsCorrect(false);

        // Act
        attemptRepository.save(savedAttempt);
        Optional<Attempt> updatedAttempt = attemptRepository.findById(savedAttempt.getId());

        // Assert
        assertTrue(updatedAttempt.isPresent());
        assertEquals("1010", updatedAttempt.get().getFeedback());
    }

    @Test
    void testDeleteAttempt() {
        // Arrange
        Attempt attempt = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("STATIC")
                .feedback("0000")
                .attemptNumber(1)
                .isCorrect(false)
                .build();
        Attempt savedAttempt = attemptRepository.save(attempt);

        // Act
        attemptRepository.delete(savedAttempt.getId());
        Optional<Attempt> deletedAttempt = attemptRepository.findById(savedAttempt.getId());

        // Assert
        assertTrue(deletedAttempt.isEmpty());
    }

    @Test
    void testCorrectAttemptTracking() {
        // Arrange
        Attempt incorrectAttempt = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("JAVA")
                .feedback("0000")
                .attemptNumber(1)
                .isCorrect(false)
                .build();
        Attempt correctAttempt = Attempt.builder()
                .gameId(testGameId)
                .guessedWord("CLASS")
                .feedback("2222")
                .attemptNumber(2)
                .isCorrect(true)
                .build();

        attemptRepository.save(incorrectAttempt);
        Attempt savedCorrect = attemptRepository.save(correctAttempt);

        // Act
        Optional<Attempt> retrievedCorrect = attemptRepository.findById(savedCorrect.getId());

        // Assert
        assertTrue(retrievedCorrect.isPresent());
        assertTrue(retrievedCorrect.get().getIsCorrect());
        assertEquals("2222", retrievedCorrect.get().getFeedback());
    }
}

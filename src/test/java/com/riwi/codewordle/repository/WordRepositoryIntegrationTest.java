package com.riwi.codewordle.repository;

import com.riwi.codewordle.domain.entities.Word;
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
 * Integration tests for WordRepository
 * Tests the repository layer with actual H2 database connection
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class WordRepositoryIntegrationTest {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        jdbcTemplate.execute("DELETE FROM words");
        jdbcTemplate.execute("DELETE FROM themes");

        // Insert test data
        jdbcTemplate.execute("INSERT INTO themes (id, name, description) VALUES (1, 'JAVA', 'Java Terms')");
        jdbcTemplate.execute("INSERT INTO themes (id, name, description) VALUES (2, 'SPRING', 'Spring Framework')");
    }

    @Test
    void testSaveWord_Success() {
        // Arrange
        Word word = Word.builder()
                .word("CLASS")
                .themeId(1L)
                .hint("OOP concept")
                .build();

        // Act
        Word savedWord = wordRepository.save(word);

        // Assert
        assertNotNull(savedWord.getId());
        assertEquals("CLASS", savedWord.getWord());
        assertEquals(1L, savedWord.getThemeId());
        assertNotNull(savedWord.getCreatedAt());
    }

    @Test
    void testFindWordById_Success() {
        // Arrange
        Word word = Word.builder()
                .word("LAMBDA")
                .themeId(1L)
                .hint("Functional expression")
                .build();
        Word savedWord = wordRepository.save(word);

        // Act
        Optional<Word> foundWord = wordRepository.findById(savedWord.getId());

        // Assert
        assertTrue(foundWord.isPresent());
        assertEquals("LAMBDA", foundWord.get().getWord());
    }

    @Test
    void testFindWordById_NotFound() {
        // Act
        Optional<Word> foundWord = wordRepository.findById(999L);

        // Assert
        assertTrue(foundWord.isEmpty());
    }

    @Test
    void testFindRandomWordByThemeId_Success() {
        // Arrange
        Word word1 = Word.builder().word("CLASS").themeId(1L).hint("Hint 1").build();
        Word word2 = Word.builder().word("ARRAY").themeId(1L).hint("Hint 2").build();
        Word word3 = Word.builder().word("BEANS").themeId(2L).hint("Hint 3").build();

        wordRepository.save(word1);
        wordRepository.save(word2);
        wordRepository.save(word3);

        // Act
        Optional<Word> randomWord = wordRepository.findRandomWordByThemeId(1L);

        // Assert
        assertTrue(randomWord.isPresent());
        assertEquals(1L, randomWord.get().getThemeId());
        assertTrue(randomWord.get().getWord().equals("CLASS") || randomWord.get().getWord().equals("ARRAY"));
    }

    @Test
    void testFindRandomWordByThemeId_EmptyTheme() {
        // Act
        Optional<Word> randomWord = wordRepository.findRandomWordByThemeId(999L);

        // Assert
        assertTrue(randomWord.isEmpty());
    }

    @Test
    void testFindAllWords() {
        // Arrange
        Word word1 = Word.builder().word("CLASS").themeId(1L).hint("Hint 1").build();
        Word word2 = Word.builder().word("ARRAY").themeId(1L).hint("Hint 2").build();

        wordRepository.save(word1);
        wordRepository.save(word2);

        // Act
        List<Word> allWords = wordRepository.findAll();

        // Assert
        assertEquals(2, allWords.size());
    }

    @Test
    void testUpdateWord() {
        // Arrange
        Word word = Word.builder().word("STATIC").themeId(1L).hint("Original hint").build();
        Word savedWord = wordRepository.save(word);

        savedWord.setHint("Updated hint");

        // Act
        wordRepository.save(savedWord);
        Optional<Word> updatedWord = wordRepository.findById(savedWord.getId());

        // Assert
        assertTrue(updatedWord.isPresent());
        assertEquals("Updated hint", updatedWord.get().getHint());
    }

    @Test
    void testDeleteWord() {
        // Arrange
        Word word = Word.builder().word("FINAL").themeId(1L).hint("Keyword").build();
        Word savedWord = wordRepository.save(word);

        // Act
        wordRepository.delete(savedWord.getId());
        Optional<Word> deletedWord = wordRepository.findById(savedWord.getId());

        // Assert
        assertTrue(deletedWord.isEmpty());
    }
}

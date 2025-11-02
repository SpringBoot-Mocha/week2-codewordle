package com.riwi.codewordle.service;

import com.riwi.codewordle.domain.entities.Attempt;
import com.riwi.codewordle.domain.entities.Game;
import com.riwi.codewordle.domain.entities.Word;
import com.riwi.codewordle.domain.enums.GameStatus;
import com.riwi.codewordle.repository.AttemptRepository;
import com.riwi.codewordle.repository.GameRepository;
import com.riwi.codewordle.repository.WordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private AttemptRepository attemptRepository;

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private GameService gameService;

    private Word testWord;
    private Game testGame;

    @BeforeEach
    void setUp() {
        // Create test data
        testWord = Word.builder()
                .id(1L)
                .word("JAVA")
                .themeId(1L)
                .hint("Programming language")
                .createdAt(LocalDateTime.now())
                .build();

        testGame = Game.builder()
                .id(1L)
                .themeId(1L)
                .wordId(1L)
                .targetWord("JAVA")
                .maxAttempts(6)
                .currentAttempts(0)
                .status(GameStatus.IN_PROGRESS)
                .won(false)
                .startedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateNewGame_Success() {
        // Arrange
        when(wordRepository.findRandomWordByThemeId(1L))
                .thenReturn(Optional.of(testWord));
        when(gameRepository.save(any(Game.class)))
                .thenReturn(testGame);

        // Act
        Game result = gameService.createNewGame(1L);

        // Assert
        assertNotNull(result);
        assertEquals("JAVA", result.getTargetWord());
        assertEquals(GameStatus.IN_PROGRESS, result.getStatus());
        assertEquals(6, result.getMaxAttempts());
        verify(wordRepository, times(1)).findRandomWordByThemeId(1L);
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void testCreateNewGame_NoWordsAvailable() {
        // Arrange
        when(wordRepository.findRandomWordByThemeId(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.createNewGame(1L);
        });
        verify(wordRepository, times(1)).findRandomWordByThemeId(1L);
        verify(gameRepository, never()).save(any());
    }

    @Test
    void testMakeGuess_CorrectGuess() {
        // Arrange
        when(gameRepository.findById(1L))
                .thenReturn(Optional.of(testGame));
        when(attemptRepository.countByGameId(1L))
                .thenReturn(0);
        when(attemptRepository.save(any(Attempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(gameRepository.save(any(Game.class)))
                .thenReturn(testGame);

        // Act
        Attempt result = gameService.makeGuess(1L, "JAVA");

        // Assert
        assertNotNull(result);
        assertEquals("JAVA", result.getGuessedWord());
        assertTrue(result.getIsCorrect());
        assertEquals("2222", result.getFeedback());
        verify(gameRepository, times(1)).findById(1L);
        verify(attemptRepository, times(1)).countByGameId(1L);
        verify(attemptRepository, times(1)).save(any(Attempt.class));
    }

    @Test
    void testMakeGuess_WrongGuess() {
        // Arrange
        when(gameRepository.findById(1L))
                .thenReturn(Optional.of(testGame));
        when(attemptRepository.countByGameId(1L))
                .thenReturn(0);
        when(attemptRepository.save(any(Attempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(gameRepository.save(any(Game.class)))
                .thenReturn(testGame);

        // Act
        Attempt result = gameService.makeGuess(1L, "RUBY");

        // Assert
        assertNotNull(result);
        assertEquals("RUBY", result.getGuessedWord());
        assertFalse(result.getIsCorrect());
        assertNotEquals("2222", result.getFeedback());
        verify(gameRepository, times(1)).findById(1L);
        verify(attemptRepository, times(1)).save(any(Attempt.class));
    }

    @Test
    void testMakeGuess_InvalidLength() {
        // Arrange
        when(gameRepository.findById(1L))
                .thenReturn(Optional.of(testGame));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.makeGuess(1L, "PYTHON"); // 6 letters, but target is 4
        });
        verify(gameRepository, times(1)).findById(1L);
        verify(attemptRepository, never()).save(any());
    }

    @Test
    void testMakeGuess_GameNotFound() {
        // Arrange
        when(gameRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.makeGuess(1L, "JAVA");
        });
        verify(gameRepository, times(1)).findById(1L);
    }

    @Test
    void testMakeGuess_GameFinished() {
        // Arrange
        Game finishedGame = Game.builder()
                .id(1L)
                .themeId(1L)
                .wordId(1L)
                .targetWord("JAVA")
                .maxAttempts(6)
                .currentAttempts(1)
                .status(GameStatus.WON)
                .won(true)
                .startedAt(LocalDateTime.now())
                .finishedAt(LocalDateTime.now())
                .build();
        when(gameRepository.findById(1L))
                .thenReturn(Optional.of(finishedGame));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            gameService.makeGuess(1L, "JAVA");
        });
        verify(gameRepository, times(1)).findById(1L);
    }

    @Test
    void testGenerateFeedback_AllCorrect() {
        // Arrange & Act
        when(wordRepository.findRandomWordByThemeId(1L))
                .thenReturn(Optional.of(testWord));
        when(gameRepository.save(any(Game.class)))
                .thenReturn(testGame);
        when(gameRepository.findById(1L))
                .thenReturn(Optional.of(testGame));
        when(attemptRepository.countByGameId(1L))
                .thenReturn(0);
        when(attemptRepository.save(any(Attempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        gameService.createNewGame(1L);
        Attempt attempt = gameService.makeGuess(1L, "JAVA");

        // Assert
        assertEquals("2222", attempt.getFeedback());
    }

    @Test
    void testGenerateFeedback_AllWrong() {
        // Arrange
        when(gameRepository.findById(1L))
                .thenReturn(Optional.of(testGame));
        when(attemptRepository.countByGameId(1L))
                .thenReturn(0);
        when(attemptRepository.save(any(Attempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(gameRepository.save(any(Game.class)))
                .thenReturn(testGame);

        // Act
        Attempt result = gameService.makeGuess(1L, "ZZZZ");

        // Assert (Z doesn't exist in JAVA)
        String feedback = result.getFeedback();
        assertTrue(feedback.matches("[0]*"));
    }

    @Test
    void testGenerateFeedback_PartialMatch() {
        // Arrange
        when(gameRepository.findById(1L))
                .thenReturn(Optional.of(testGame));
        when(attemptRepository.countByGameId(1L))
                .thenReturn(0);
        when(attemptRepository.save(any(Attempt.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(gameRepository.save(any(Game.class)))
                .thenReturn(testGame);

        // Act
        Attempt result = gameService.makeGuess(1L, "AVAJ"); // Reversed

        // Assert - A is in position 1 and 3, V is in position 2
        String feedback = result.getFeedback();
        assertNotNull(feedback);
        assertTrue(feedback.contains("1")); // Some letters in wrong positions
    }

    @Test
    void testGetGameAttempts() {
        // Arrange
        Attempt attempt1 = Attempt.builder()
                .id(1L)
                .gameId(1L)
                .guessedWord("TEST")
                .isCorrect(false)
                .attemptNumber(1)
                .build();

        when(attemptRepository.findByGameId(1L))
                .thenReturn(java.util.List.of(attempt1));

        // Act
        var result = gameService.getGameAttempts(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST", result.get(0).getGuessedWord());
    }

    @Test
    void testGetGameStatus() {
        // Arrange
        when(gameRepository.findById(1L))
                .thenReturn(Optional.of(testGame));

        // Act
        Optional<Game> result = gameService.getGameStatus(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("JAVA", result.get().getTargetWord());
        verify(gameRepository, times(1)).findById(1L);
    }
}

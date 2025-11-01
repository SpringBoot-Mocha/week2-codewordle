package com.riwi.codewordle.service;

import com.riwi.codewordle.domain.entities.Attempt;
import com.riwi.codewordle.domain.entities.Game;
import com.riwi.codewordle.domain.entities.Word;
import com.riwi.codewordle.domain.enums.GameStatus;
import com.riwi.codewordle.repository.AttemptRepository;
import com.riwi.codewordle.repository.GameRepository;
import com.riwi.codewordle.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final AttemptRepository attemptRepository;
    private final WordRepository wordRepository;

    private static final Integer MAX_ATTEMPTS = 6;

    /**
     * Create a new game
     * @param themeId Selected theme ID
     * @return The created game
     */
    public Game createNewGame(Long themeId) {
        // Get a random word from the theme
        Optional<Word> randomWord = wordRepository.findRandomWordByThemeId(themeId);
        
        if (randomWord.isEmpty()) {
            throw new IllegalArgumentException("No words available for theme: " + themeId);
        }

        Word word = randomWord.get();
        
        // Create the new game
        Game game = Game.builder()
                .themeId(themeId)
                .wordId(word.getId())
                .targetWord(word.getWord())
                .maxAttempts(MAX_ATTEMPTS)
                .currentAttempts(0)
                .status(GameStatus.IN_PROGRESS)
                .won(false)
                .startedAt(LocalDateTime.now())
                .build();

        return gameRepository.save(game);
    }

    /**
     * Get a game by ID
     * @param gameId Game ID
     * @return The found game
     */
    public Optional<Game> getGameById(Long gameId) {
        return gameRepository.findById(gameId);
    }

    /**
     * Process a player's guess
     * @param gameId Game ID
     * @param guessedWord Word guessed by the player
     * @return The saved attempt with feedback
     */
    public Attempt makeGuess(Long gameId, String guessedWord) {
        // Get the game
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        // Validate that the game is in progress
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("The game has already finished");
        }

        // Normalize input
        String normalizedGuess = guessedWord.toUpperCase().trim();

        // Validate length
        if (normalizedGuess.length() != game.getTargetWord().length()) {
            throw new IllegalArgumentException("The word must have " + game.getTargetWord().length() + " letters");
        }

        // Increment attempt number
        Integer attemptNumber = attemptRepository.countByGameId(gameId) + 1;

        // Check if it's correct
        boolean isCorrect = normalizedGuess.equals(game.getTargetWord());

        // Generate feedback
        String feedback = generateFeedback(normalizedGuess, game.getTargetWord());

        // Create the attempt
        Attempt attempt = Attempt.builder()
                .gameId(gameId)
                .guessedWord(normalizedGuess)
                .feedback(feedback)
                .attemptNumber(attemptNumber)
                .isCorrect(isCorrect)
                .attemptedAt(LocalDateTime.now())
                .build();

        // Save the attempt
        Attempt savedAttempt = attemptRepository.save(attempt);

        // Update the game
        game.setCurrentAttempts(attemptNumber);

        if (isCorrect) {
            // Player won!
            game.setStatus(GameStatus.WON);
            game.setWon(true);
            game.setFinishedAt(LocalDateTime.now());
        } else if (attemptNumber >= game.getMaxAttempts()) {
            // Player lost (reached the limit)
            game.setStatus(GameStatus.LOST);
            game.setWon(false);
            game.setFinishedAt(LocalDateTime.now());
        }

        gameRepository.save(game);

        return savedAttempt;
    }

    /**
     * Generate visual feedback for the player
     * Compares each letter of the guess with the target word
     * 
     * Convention:
     * 🟩 Green: correct letter in correct position (2)
     * 🟨 Yellow: correct letter in wrong position (1)
     * ⬜ Gray: letter doesn't exist (0)
     * 
     * @param guess Guessed word
     * @param target Target word
     * @return String with numbers: 0=gray, 1=yellow, 2=green
     */
    private String generateFeedback(String guess, String target) {
        StringBuilder feedback = new StringBuilder();
        char[] targetChars = target.toCharArray();
        char[] guessChars = guess.toCharArray();

        // First pass: mark greens (exact matches)
        for (int i = 0; i < guessChars.length; i++) {
            if (guessChars[i] == targetChars[i]) {
                feedback.append("2");
                targetChars[i] = '\0'; // Mark as used
            } else {
                feedback.append("0"); // Placeholder
            }
        }

        // Second pass: mark yellows (correct letter, wrong position)
        StringBuilder finalFeedback = new StringBuilder();
        for (int i = 0; i < guessChars.length; i++) {
            if (feedback.charAt(i) == '2') {
                finalFeedback.append("2");
            } else {
                boolean found = false;
                for (int j = 0; j < targetChars.length; j++) {
                    if (guessChars[i] == targetChars[j]) {
                        finalFeedback.append("1");
                        targetChars[j] = '\0'; // Mark as used
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    finalFeedback.append("0");
                }
            }
        }

        return finalFeedback.toString();
    }

    /**
     * Get the attempt history of a game
     * @param gameId Game ID
     * @return List of attempts
     */
    public List<Attempt> getGameAttempts(Long gameId) {
        return attemptRepository.findByGameId(gameId);
    }

    /**
     * Get the current status of a game
     * @param gameId Game ID
     * @return The game with its updated status
     */
    public Optional<Game> getGameStatus(Long gameId) {
        return gameRepository.findById(gameId);
    }

    /**
     * Get all games
     * @return List of all games
     */
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    /**
     * Get in-progress games
     * @return List of in-progress games
     */
    public List<Game> getInProgressGames() {
        return gameRepository.findByStatus(GameStatus.IN_PROGRESS);
    }

    /**
     * Get won games
     * @return List of won games
     */
    public List<Game> getWonGames() {
        return gameRepository.findByStatus(GameStatus.WON);
    }

    /**
     * Get lost games
     * @return List of lost games
     */
    public List<Game> getLostGames() {
        return gameRepository.findByStatus(GameStatus.LOST);
    }
}

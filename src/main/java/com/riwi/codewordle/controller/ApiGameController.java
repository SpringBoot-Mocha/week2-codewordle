package com.riwi.codewordle.controller;

import com.riwi.codewordle.domain.entities.Attempt;
import com.riwi.codewordle.domain.entities.Game;
import com.riwi.codewordle.domain.entities.Theme;
import com.riwi.codewordle.repository.ThemeRepository;
import com.riwi.codewordle.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Tag(name = "Game Controller", description = "API for managing CodeWordle games")
public class ApiGameController {

    private final GameService gameService;
    private final ThemeRepository themeRepository;

    /**
     * Create a new game
     */
    @PostMapping
    @Operation(summary = "Create a new game", description = "Start a new game with a selected theme")
    public ResponseEntity<?> createGame(
            @Parameter(description = "Theme ID") @RequestParam Long themeId) {
        try {
            Game game = gameService.createNewGame(themeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Game created successfully");
            response.put("gameId", game.getId());
            response.put("theme", game.getThemeId());
            response.put("maxAttempts", game.getMaxAttempts());
            response.put("wordLength", game.getTargetWord().length());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get game status
     */
    @GetMapping("/{gameId}")
    @Operation(summary = "Get game status", description = "Retrieve the current status of a game")
    public ResponseEntity<?> getGameStatus(
            @Parameter(description = "Game ID") @PathVariable Long gameId) {
        Optional<Game> game = gameService.getGameStatus(gameId);
        
        if (game.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "Game not found"
                    ));
        }

        Game gameData = game.get();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("gameId", gameData.getId());
        response.put("status", gameData.getStatus());
        response.put("currentAttempts", gameData.getCurrentAttempts());
        response.put("maxAttempts", gameData.getMaxAttempts());
        response.put("won", gameData.getWon());
        response.put("wordLength", gameData.getTargetWord().length());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Make a guess
     */
    @PostMapping("/{gameId}/guess")
    @Operation(summary = "Make a guess", description = "Submit a word guess for the game")
    public ResponseEntity<?> makeGuess(
            @Parameter(description = "Game ID") @PathVariable Long gameId,
            @Parameter(description = "Guessed word") @RequestParam String word) {
        try {
            Attempt attempt = gameService.makeGuess(gameId, word);
            
            // Get updated game status
            Optional<Game> gameOpt = gameService.getGameStatus(gameId);
            Game game = gameOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("attempt", attempt.getAttemptNumber());
            response.put("guessedWord", attempt.getGuessedWord());
            response.put("feedback", attempt.getFeedback());
            response.put("isCorrect", attempt.getIsCorrect());
            response.put("gameStatus", game.getStatus());
            response.put("currentAttempts", game.getCurrentAttempts());
            response.put("maxAttempts", game.getMaxAttempts());
            response.put("won", game.getWon());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get game attempts history
     */
    @GetMapping("/{gameId}/attempts")
    @Operation(summary = "Get attempts history", description = "Retrieve all attempts made in a game")
    public ResponseEntity<?> getGameAttempts(
            @Parameter(description = "Game ID") @PathVariable Long gameId) {
        Optional<Game> game = gameService.getGameStatus(gameId);
        
        if (game.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "Game not found"
                    ));
        }

        List<Attempt> attempts = gameService.getGameAttempts(gameId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("gameId", gameId);
        response.put("attempts", attempts);
        response.put("totalAttempts", attempts.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all available themes
     */
    @GetMapping("/themes")
    @Operation(summary = "Get available themes", description = "Retrieve all available game themes")
    public ResponseEntity<?> getThemes() {
        List<Theme> themes = themeRepository.findAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("themes", themes);
        response.put("total", themes.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all games
     */
    @GetMapping
    @Operation(summary = "Get all games", description = "Retrieve all games in the system")
    public ResponseEntity<?> getAllGames() {
        List<Game> games = gameService.getAllGames();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("games", games);
        response.put("total", games.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get in-progress games
     */
    @GetMapping("/status/in-progress")
    @Operation(summary = "Get in-progress games", description = "Retrieve all games currently in progress")
    public ResponseEntity<?> getInProgressGames() {
        List<Game> games = gameService.getInProgressGames();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("games", games);
        response.put("total", games.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get won games
     */
    @GetMapping("/status/won")
    @Operation(summary = "Get won games", description = "Retrieve all games that were won")
    public ResponseEntity<?> getWonGames() {
        List<Game> games = gameService.getWonGames();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("games", games);
        response.put("total", games.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get lost games
     */
    @GetMapping("/status/lost")
    @Operation(summary = "Get lost games", description = "Retrieve all games that were lost")
    public ResponseEntity<?> getLostGames() {
        List<Game> games = gameService.getLostGames();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("games", games);
        response.put("total", games.size());
        
        return ResponseEntity.ok(response);
    }
}

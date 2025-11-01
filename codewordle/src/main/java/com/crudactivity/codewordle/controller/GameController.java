package com.crudactivity.codewordle.controller;

import com.crudactivity.codewordle.model.AttemptModel;
import com.crudactivity.codewordle.model.GameModel;
import com.crudactivity.codewordle.service.AttemptService;
import com.crudactivity.codewordle.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/game")
public class GameController {



    private final GameService gameService;
    private final AttemptService attemptService;

    // Si quieres mantener un ID de partida "actual" por sesión, lo ideal es guardarlo en session
    // Por simplicidad inicial, lo mantendremos como parámetro o en modelo.
    public GameController(GameService gameService, AttemptService attemptService) {
        this.gameService = gameService;
        this.attemptService = attemptService;
    }
    @GetMapping("/")
    public String index() {
        return "/game/start";
    }

    // Inicia una nueva partida y redirige a la vista del juego con id de la partida
    @GetMapping("/start")
    public String start(@RequestParam(defaultValue = "Java") String topic, Model model) {
        GameModel game = gameService.startNewGame(topic);
        model.addAttribute("game", game);
        model.addAttribute("attempts", attemptService.getAttemptsByGame(game.getId_game()));
        model.addAttribute("message", "Nueva partida iniciada. Tema: " + topic);
        return "/game/game"; // coincide con tu carpeta WEB-INF/game/game.jsp
    }

    // Procesa intento; recibe id_game y el intento
    @PostMapping("/attempt")
    public String attempt(@RequestParam int id_game, @RequestParam String attempt, Model model) {
        // recupera partida
        var opt = gameService.getGame(id_game);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Partida no encontrada");
            return "/game/game";
        }
        GameModel game = opt.get();

        // evaluar
        String result = gameService.evaluateAttempt(attempt, game.getHidden_word());

        // guardar intento
        AttemptModel a = new AttemptModel();
        a.setId_game(id_game);
        a.setAttempt(attempt);
        a.setResult(result);
        attemptService.saveAttempt(a);

        // incrementar contador de intentos en game
        gameService.incrementAttempts(id_game);

        // refrescar datos
        model.addAttribute("game", gameService.getGame(id_game).get());
        List<AttemptModel> attempts = attemptService.getAttemptsByGame(id_game);
        model.addAttribute("attempts", attempts);
        model.addAttribute("lastResult", result);

        // estado de finalización
        boolean won = attempt.equalsIgnoreCase(game.getHidden_word());
        if (won) {
            gameService.changeState(id_game, "WON");
            model.addAttribute("message", "¡Ganaste! La palabra era " + game.getHidden_word());
        } else if (attempts.size() >= 6) {
            gameService.changeState(id_game, "LOST");
            model.addAttribute("message", "Has perdido. La palabra era " + game.getHidden_word());
        } else {
            model.addAttribute("message", "Intento registrado: " + attempts.size());
        }

        return "/game/game";
    }
}

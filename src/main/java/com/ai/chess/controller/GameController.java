package com.ai.chess.controller;

import com.ai.chess.domain.entity.Game;
import com.ai.chess.domain.request.MoveRequest;
import com.ai.chess.service.ChessService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class GameController {

    private final ChessService chessService;

    /**
     * Displays the home page of the chess game application.
     *
     * @return the name of the home page template
     */
    @GetMapping("/")
    public String showHomePage() {
        return "home";
    }

    /**
     * Starts a new chess game based on the player's chosen side (white, black, or random).
     *
     * @param side the side chosen by the player (white, black, or random)
     * @param session the current HTTP session used to associate the game with the user
     * @return a redirect URL to the new game's page
     */
    @PostMapping("/game/start")
    public String createNewGame(@RequestParam(value = "side") String side, HttpSession session) {
        Game game = chessService.createGame(session.getId(), side);
        return "redirect:/game/" + game.getId();
    }

    /**
     * Displays the game page for a specific game identified by the game ID.
     *
     * @param gameId the unique identifier of the game
     * @param model  the model object used to pass game data to the view
     * @return the name of the game page template
     */
    @GetMapping("/game/{gameId}")
    public String showGamePage(@PathVariable String gameId, Model model) {
        Game game = chessService.findGameById(gameId);
        model.addAttribute("game", game);
        return "game";
    }

    /**
     * Retrieves a list of possible moves for a given position on the board.
     * This is an API endpoint.
     *
     * @param gameId   the unique identifier of the game
     * @param position the current position of the piece on the board
     * @return a list of possible moves
     */
    @GetMapping("/api/game/{gameId}/possibleMoves")
    public @ResponseBody List<String> retrievePossibleMoves(@PathVariable String gameId,
                                                            @RequestParam String position) {
        return chessService.getPossibleMoves(gameId, position);
    }

    /**
     * Processes a move request for a specified game.
     *
     * @param moveRequest the request object containing details of the move to be made
     * @return the updated Game object after the move is applied
     */
    @PostMapping("/api/game/move")
    public @ResponseBody Game handleMoveRequest(@RequestBody MoveRequest moveRequest) {
        return chessService.makeMove(moveRequest);
    }

}

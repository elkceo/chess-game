package com.ai.chess.service.impl;

import com.ai.chess.constants.Side;
import com.ai.chess.domain.entity.Game;
import com.ai.chess.domain.request.MoveRequest;
import com.ai.chess.repository.GameRepository;
import com.ai.chess.service.ChessService;
import com.ai.chess.service.GeminiService;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ChessServiceImpl implements ChessService {

    private final GeminiService geminiService;
    private final GameRepository gameRepository;
    public static final String DEFAULT_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    @Override
    public Game findGameById(String gameId) {
        return gameRepository.findById(gameId).orElseThrow(
                () -> new RuntimeException("Game not found with id: " + gameId)
        );
    }

    @Override
    public Game createGame(String sessionId, String side) {
        Side playerSide;
        switch (side) {
            case "white" -> playerSide = Side.WHITE;
            case "black" -> playerSide = Side.BLACK;
            default -> playerSide = Math.random() < 0.5 ? Side.WHITE : Side.BLACK;
        }
        Game game = new Game();
        game.setSessionId(sessionId);
        game.setCreatedAt(System.currentTimeMillis());
        game.setCurrentFEN(DEFAULT_FEN);
        game.setCurrentPlayer(Side.WHITE);
        game.setPlayerSide(playerSide);
        game.setGameOver(false);
        game.setMoves(new ArrayList<>());
        gameRepository.save(game);
        if (playerSide == Side.BLACK) {
            MoveRequest moveRequest = new MoveRequest(game.getId(), null, null, true);
            return makeMove(moveRequest);
        } else {
            return game;
        }
    }

    @Override
    public List<String> getPossibleMoves(String gameId, String position) {
        Game game = findGameById(gameId);
        List<Move> legalMoves = getLegalMoves(game.getCurrentFEN());
        List<String> possibleMoves = new ArrayList<>();
        legalMoves.stream()
                .filter(move -> move.getFrom().toString().equalsIgnoreCase(position)).forEach(move -> {
                    possibleMoves.add(move.getTo().toString().toLowerCase());
                });
        return possibleMoves;
    }

    @Override
    public Game makeMove(MoveRequest moveRequest) {
        Game game = findGameById(moveRequest.getGameId());
        Board board = new Board();
        String currentFEN = game.getCurrentFEN();
        board.loadFromFen(currentFEN);
        Move move = getMoveBasedOnRequest(moveRequest, game);
        board.doMove(move);

        if (gameIsOver(board)) {
            game.setGameOver(true);
            game.setWinner(game.getCurrentPlayer());
            game.setFinishedAt(System.currentTimeMillis());
        }

        game.setCurrentFEN(board.getFen());
        game.setCurrentPlayer(game.getCurrentPlayer() == Side.WHITE ? Side.BLACK : Side.WHITE);
        game.setUpdatedAt(System.currentTimeMillis());
        game.getMoves().add(move.toString());

        return gameRepository.save(game);
    }

    private Move getMoveBasedOnRequest(MoveRequest moveRequest, Game game) {
        Square from;
        Square to;

        if (moveRequest.isGeminiMove()) {
            String geminiMove = getGeminiMove(game.getCurrentPlayer(), game.getCurrentFEN()).toUpperCase();
            from = Square.fromValue(geminiMove.substring(0, 2));
            to = Square.fromValue(geminiMove.substring(2));
        } else {
            from = Square.fromValue(moveRequest.getFrom().toUpperCase());
            to = Square.fromValue(moveRequest.getTo().toUpperCase());
        }

        return new Move(from, to);
    }

    @SneakyThrows
    @Override
    public String getGeminiMove(Side side, String FEN) {
        List<Move> legalMoves = getLegalMoves(FEN);

        StringBuilder moves = new StringBuilder();
        legalMoves.forEach(move -> moves.append(move.toString()).append(", "));
        log.debug("Legal moves: {}", moves);

        String prompt = String.format("You are playing chess as %s. " +
                "The current board state is [%s]. " +
                "Play smart and select best one of the following moves: %s" +
                "Reply only with the move in strict format {from square}{to square} without brackets e.g. e2e4. " +
                "You can reply only with one of the moves listed above.", side, FEN, moves);

        String geminiMove = requestValidGeminiMove(prompt, legalMoves);
        log.debug("Gemini move: {}", geminiMove);
        return geminiMove;
    }

    private String requestValidGeminiMove(String prompt, List<Move> legalMoves) throws IOException {
        boolean contains = false;
        String geminiMove;
        do {
            geminiMove = geminiService.generateContent(prompt).trim();
            log.debug("Gemini response for move: {}", geminiMove);
            for (Move move : legalMoves) {
                if (geminiMove.equalsIgnoreCase(move.toString())) {
                    contains = true;
                    break;
                }
            }
        } while (!contains);
        return geminiMove;
    }

    private List<Move> getLegalMoves(String FEN) {
        Board board = new Board();
        board.loadFromFen(FEN);
        return board.legalMoves();
    }

    private boolean gameIsOver(Board board) {
        return board.isMated() || board.isStaleMate() || board.isDraw();
    }

}

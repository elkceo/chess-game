package com.ai.chess;

import com.ai.chess.constants.Side;
import com.ai.chess.domain.entity.Game;
import com.ai.chess.domain.request.MoveRequest;
import com.ai.chess.repository.GameRepository;
import com.ai.chess.service.GeminiService;
import com.ai.chess.service.impl.ChessServiceImpl;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChessServiceImplTest {

    @Mock
    private GeminiService geminiService;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private ChessServiceImpl chessService;

    private Game testGame;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testGame = new Game();
        testGame.setId("gameId");
        testGame.setSessionId("sessionId");
        testGame.setCurrentFEN(ChessServiceImpl.DEFAULT_FEN);
        testGame.setCurrentPlayer(Side.WHITE);
        testGame.setPlayerSide(Side.WHITE);
        testGame.setGameOver(false);
        testGame.setMoves(new ArrayList<>());
    }

    @Test
    void testCreateGame() {
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        Game createdGame = chessService.createGame("sessionId", "white");

        assertNotNull(createdGame);
        assertEquals("sessionId", createdGame.getSessionId());
        assertEquals(Side.WHITE, createdGame.getPlayerSide());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void testFindGameById() {
        when(gameRepository.findById("gameId")).thenReturn(Optional.of(testGame));

        Game foundGame = chessService.findGameById("gameId");

        assertNotNull(foundGame);
        assertEquals("gameId", foundGame.getId());
        verify(gameRepository, times(1)).findById("gameId");
    }

    @Test
    void testFindGameById_NotFound() {
        when(gameRepository.findById("gameId")).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            chessService.findGameById("gameId");
        });

        assertEquals("Game not found with id: gameId", thrown.getMessage());
    }

    @Test
    void testMakeMove() throws IOException {
        MoveRequest moveRequest = new MoveRequest("gameId", "e2", "e4", false);
        when(gameRepository.findById("gameId")).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        Move move = new Move(Square.E2, Square.E4);
        when(geminiService.generateContent(any())).thenReturn("e2e4");

        Game updatedGame = chessService.makeMove(moveRequest);
        String expectedFEN = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        assertEquals(expectedFEN, updatedGame.getCurrentFEN());
        assertEquals(Side.BLACK, updatedGame.getCurrentPlayer());
        assertTrue(updatedGame.getMoves().contains(move.toString()));
        verify(gameRepository, times(1)).save(updatedGame);
    }

    @Test
    void testGetPossibleMoves() throws IOException {
        when(gameRepository.findById("gameId")).thenReturn(Optional.of(testGame));
        when(geminiService.generateContent(any())).thenReturn("e2e4");

        List<String> possibleMoves = chessService.getPossibleMoves("gameId", "e2");

        assertNotNull(possibleMoves);
        assertFalse(possibleMoves.isEmpty());
        verify(gameRepository, times(1)).findById("gameId");
    }


}

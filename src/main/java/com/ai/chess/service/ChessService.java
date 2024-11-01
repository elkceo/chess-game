package com.ai.chess.service;

import com.ai.chess.constants.Side;
import com.ai.chess.domain.entity.Game;
import com.ai.chess.domain.request.MoveRequest;

import java.util.List;

public interface ChessService {
    String getGeminiMove(Side side, String FEN);

    Game findGameById(String gameId);

    Game createGame(String sessionId, String side);

    List<String> getPossibleMoves(String game, String position);

    Game makeMove(MoveRequest moveRequest);
}

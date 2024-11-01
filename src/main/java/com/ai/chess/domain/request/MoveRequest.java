package com.ai.chess.domain.request;

import lombok.Data;

@Data
public class MoveRequest {
    private String gameId;
    private String from;
    private String to;
    private boolean isGeminiMove;

    public MoveRequest(String gameId, String from, String to, boolean isGeminiMove) {
        this.gameId = gameId;
        this.from = from;
        this.to = to;
        this.isGeminiMove = isGeminiMove;
    }

}

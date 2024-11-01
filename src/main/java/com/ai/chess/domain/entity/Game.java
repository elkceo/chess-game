package com.ai.chess.domain.entity;

import com.ai.chess.constants.Side;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "games")
public class Game {
    @Id
    private String id;
    private String sessionId;
    private Long createdAt;
    private Long updatedAt;
    private Long finishedAt;
    private String currentFEN;
    private Side playerSide;
    private Side currentPlayer;
    private Side winner;
    private Boolean gameOver;
    private List<String> moves;
}

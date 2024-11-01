package com.ai.chess.repository;

import com.ai.chess.domain.entity.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {
    Game findBySessionId(String sessionId);
}

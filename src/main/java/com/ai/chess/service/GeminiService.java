package com.ai.chess.service;

import java.io.IOException;

public interface GeminiService {
    String generateContent(String text) throws IOException;
}

package com.ai.chess.service.impl;

import com.ai.chess.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {
    @Value("${google.api.key}")
    private String apiKey;
    public static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=";
    private final OkHttpClient client = new OkHttpClient();
    private static final String CONTENTS = "contents";
    private static final String PARTS = "parts";
    private static final String TEXT = "text";
    private static final String CANDIDATES = "candidates";
    private static final String CONTENT = "content";
    @Override
    public String generateContent(String prompt) throws IOException {
        JSONObject body = buildRequestBody(prompt);

        RequestBody requestBody = RequestBody.create(body.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(URL + apiKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.error("Unsuccessful response from Gemini API: {}", response);
                throw new IOException("Failed to retrieve response from Gemini API");
            }
            return parseResponse(response.body().string());
        }
    }

    private JSONObject buildRequestBody(String prompt) {
        JSONObject body = new JSONObject();
        JSONArray contentArray = new JSONArray()
                .put(new JSONObject().put(PARTS, new JSONArray().put(new JSONObject().put(TEXT, prompt))));
        body.put(CONTENTS, contentArray);
        return body;
    }
    private String parseResponse(String responseString) throws JSONException {
        JSONObject responseJson = new JSONObject(responseString);
        log.debug("Response: {}", responseJson);

        try {
            return responseJson.getJSONArray(CANDIDATES)
                    .getJSONObject(0)
                    .getJSONObject(CONTENT)
                    .getJSONArray(PARTS)
                    .getJSONObject(0)
                    .getString(TEXT)
                    .trim();
        } catch (JSONException e) {
            log.error("Error parsing Gemini response: {}", e.getMessage(), e);
            throw e;
        }
    }
}

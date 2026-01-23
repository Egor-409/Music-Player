package com.telegram.musicplayer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TelegramFileService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public String getFileUrl(String fileId) {
        try {
            // 1️⃣ getFile
            String getFileUrl =
                    "https://api.telegram.org/bot" + botToken +
                    "/getFile?file_id=" + fileId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getFileUrl))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());

            if (!root.get("ok").asBoolean()) {
                throw new RuntimeException("Telegram getFile failed");
            }

            // 2️⃣ file_path
            String filePath =
                    root.get("result").get("file_path").asText();

            // 3️⃣ CDN URL
            return "https://api.telegram.org/file/bot"
                    + botToken + "/" + filePath;

        } catch (Exception e) {
            throw new RuntimeException("Cannot get Telegram file URL", e);
        }
    }
}

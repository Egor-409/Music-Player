package com.telegram.musicplayer.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.musicplayer.model.TelegramUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.web.client.RestTemplate;


@Service
public class TelegramFileService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private static final String TELEGRAM_API = "https://api.telegram.org/bot";

    public String getFileUrl(String fileId) {
        try {
            String getFileUrl =
                    TELEGRAM_API + botToken + "/getFile?file_id=" + fileId;

            RestTemplate restTemplate = new RestTemplate();
            Map response = restTemplate.getForObject(getFileUrl, Map.class);

            Map result = (Map) response.get("result");
            String filePath = (String) result.get("file_path");

            return "https://api.telegram.org/file/bot" + botToken + "/" + filePath;

        } catch (Exception e) {
            throw new RuntimeException("Failed to get telegram file", e);
        }
    }
}


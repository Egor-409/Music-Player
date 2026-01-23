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

@Service
public class TelegramAuthService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelegramUser parseAndValidate(String initData) {
        try {
            // 1️⃣ parse query string
            Map<String, String> data = new HashMap<>();
            for (String pair : initData.split("&")) {
                String[] kv = pair.split("=", 2);
                data.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }

            String hash = data.remove("hash");
            if (hash == null) {
                throw new RuntimeException("hash missing");
            }

            // 2️⃣ data_check_string
            String dataCheckString = data.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");

            // 3️⃣ secret key = HMAC_SHA256("WebAppData", botToken)
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    "WebAppData".getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            ));
            byte[] secretKey = mac.doFinal(botToken.getBytes(StandardCharsets.UTF_8));

            // 4️⃣ calculate hash
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] calculated = mac.doFinal(
                    dataCheckString.getBytes(StandardCharsets.UTF_8)
            );

            String calculatedHex = bytesToHex(calculated);

            if (!calculatedHex.equals(hash)) {
                throw new RuntimeException("Invalid Telegram hash");
            }

            // 5️⃣ parse user
            String userJson = data.get("user");
            Map<String, Object> userMap =
                    objectMapper.readValue(userJson, Map.class);

            Long userId = ((Number) userMap.get("id")).longValue();
            return new TelegramUser(userId);

        } catch (Exception e) {
            throw new RuntimeException("Invalid initData", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

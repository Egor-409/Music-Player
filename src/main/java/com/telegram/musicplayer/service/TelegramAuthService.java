package com.telegram.musicplayer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.musicplayer.model.TelegramUser;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TelegramAuthService {

    private final String botToken = "8061375121:AAEHxqFOLMHeRoi-sk9K85b42ZuYZVsW8m8";

    public TelegramUser parse(String initData) {
        try {
            // 1️⃣ парсим initData
            Map<String, String> data = Arrays.stream(initData.split("&"))
                    .map(p -> p.split("=", 2))
                    .collect(Collectors.toMap(
                            p -> p[0],
                            p -> URLDecoder.decode(p[1], StandardCharsets.UTF_8)
                    ));

            // 2️⃣ проверка hash
            String hash = data.remove("hash");

            String dataCheckString = data.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));

            byte[] secretKey = MessageDigest
                    .getInstance("SHA-256")
                    .digest(botToken.getBytes(StandardCharsets.UTF_8));

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] calculatedHash = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            String calculatedHex = bytesToHex(calculatedHash);

            if (!calculatedHex.equals(hash)) {
                throw new RuntimeException("Invalid Telegram hash");
            }

            // 3️⃣ парсим пользователя
            String userJson = data.get("user");

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> userMap = mapper.readValue(userJson, Map.class);

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

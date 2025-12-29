package com.telegram.musicplayer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.musicplayer.model.TelegramUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TelegramAuthService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelegramUser parseAndValidate(String initData) {
        try {
            Map<String, String> data = Arrays.stream(initData.split("&"))
                    .map(p -> p.split("=", 2))
                    .collect(Collectors.toMap(
                            p -> p[0],
                            p -> URLDecoder.decode(p[1], StandardCharsets.UTF_8)
                    ));

            // ✅ Забираем hash
            String hash = data.remove("hash");

            // ❗️ОЧЕНЬ ВАЖНО — УДАЛЯЕМ signature
            data.remove("signature");

            if (hash == null) {
                throw new RuntimeException("Hash is missing");
            }

            // ✅ data_check_string
            String dataCheckString = data.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));

            // ✅ secret_key = SHA256(botToken)
            byte[] secretKey = MessageDigest
                    .getInstance("SHA-256")
                    .digest(botToken.getBytes(StandardCharsets.UTF_8));

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] calculatedHash = mac.doFinal(
                    dataCheckString.getBytes(StandardCharsets.UTF_8)
            );

            String calculatedHex = HexFormat.of().formatHex(calculatedHash);

            if (!calculatedHex.equals(hash)) {
                throw new RuntimeException("Invalid Telegram hash");
            }

            // ✅ Парсим user
            String userJson = data.get("user");
            Map<String, Object> userMap =
                    objectMapper.readValue(userJson, Map.class);

            Long userId = ((Number) userMap.get("id")).longValue();

            return new TelegramUser(userId);

        } catch (Exception e) {
            throw new RuntimeException("Invalid initData", e);
        }
    }
}

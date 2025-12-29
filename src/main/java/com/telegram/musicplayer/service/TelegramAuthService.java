package com.telegram.musicplayer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.musicplayer.model.TelegramUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TelegramAuthService {

    private static final Logger log =
            LoggerFactory.getLogger(TelegramAuthService.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelegramUser parseAndValidate(String initData) {
        try {
            log.info("MINI APP REQUEST RECEIVED");
            log.info("INIT DATA RAW = {}", initData);

            // 1️⃣ parse initData
            Map<String, String> data = Arrays.stream(initData.split("&"))
                    .map(p -> p.split("=", 2))
                    .collect(Collectors.toMap(
                            p -> p[0],
                            p -> URLDecoder.decode(p[1], StandardCharsets.UTF_8)
                    ));

            // 2️⃣ get hash
            String receivedHash = data.remove("hash");
            if (receivedHash == null) {
                throw new RuntimeException("hash missing");
            }

            // 3️⃣ build data_check_string
            List<String> sorted = data.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .toList();

            String dataCheckString = String.join("\n", sorted);

            log.info("DATA CHECK STRING:\n{}", dataCheckString);

            // 4️⃣ secret_key = SHA256(botToken)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] secretKey = digest.digest(
                    botToken.getBytes(StandardCharsets.UTF_8)
            );

            // 5️⃣ HMAC-SHA256
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));

            String calculatedHash = HexFormat.of().formatHex(
                    mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8))
            );

            log.info("HASH FROM TG      = {}", receivedHash);
            log.info("HASH CALCULATED   = {}", calculatedHash);

            if (!calculatedHash.equals(receivedHash)) {
                throw new RuntimeException("Invalid initData");
            }

            // 6️⃣ parse user
            TelegramUser user = objectMapper.readValue(
                    data.get("user"),
                    TelegramUser.class
            );

            log.info("AUTH OK userId={}", user.getId());
            return user;

        } catch (Exception e) {
            log.error("INIT DATA VALIDATION FAILED", e);
            throw new RuntimeException("Invalid initData");
        }
    }
}

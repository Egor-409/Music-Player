package com.telegram.musicplayer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.musicplayer.model.TelegramUser;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TelegramAuthService {

    private final String botToken = "BOT_TOKEN";

    public TelegramUser parse(String initData) {
        Map<String, String> data = Arrays.stream(initData.split("&"))
                .map(p -> p.split("=", 2))
                .collect(Collectors.toMap(
                        p -> p[0],
                        p -> URLDecoder.decode(p[1], StandardCharsets.UTF_8)
                ));

        String userJson = data.get("user");

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> userMap = mapper.readValue(userJson, Map.class);
            Long userId = ((Number) userMap.get("id")).longValue();

            return new TelegramUser(userId);

        } catch (Exception e) {
            throw new RuntimeException("Invalid initData", e);
        }
    }
}

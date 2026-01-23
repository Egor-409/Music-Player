package com.telegram.musicplayer.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TelegramInitDataParser {

    public static Long extractUserId(String initData) {
        try {
            String decoded = URLDecoder.decode(initData, StandardCharsets.UTF_8);
            return Arrays.stream(decoded.split("&"))
                    .filter(p -> p.startsWith("user="))
                    .findFirst()
                    .map(p -> p.substring(5))
                    .map(json -> json.replaceAll(".*\"id\":(\\d+).*", "$1"))
                    .map(Long::parseLong)
                    .orElseThrow();
        } catch (Exception e) {
            throw new RuntimeException("Invalid Telegram initData");
        }
    }
}

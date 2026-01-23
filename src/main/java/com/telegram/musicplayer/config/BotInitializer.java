package com.telegram.musicplayer.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import com.telegram.musicplayer.bot.MusicBot;

@Configuration
@RequiredArgsConstructor
public class BotInitializer {

    private final MusicBot musicBot;

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(musicBot);
            System.out.println(">>> Telegram Bot successfully started!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

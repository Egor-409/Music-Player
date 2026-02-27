package com.telegram.musicplayer.bot;

import com.telegram.musicplayer.config.BotConfig;
import com.telegram.musicplayer.model.Track;
import com.telegram.musicplayer.service.TrackService;
import com.telegram.musicplayer.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;


@Slf4j
@Component
@RequiredArgsConstructor
public class MusicBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final TrackService trackService;
    private final UserService userService;

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("UPDATE RECEIVED: " + update);

        if (update.hasMessage()) {
            Message msg = update.getMessage();

            // ---------- Mini App кнопка ----------
            if (msg.hasText() && msg.getText().equals("/app")) {
                sendMiniAppButton(msg.getChatId());
                return;
            }

            // ---------- Загрузка трека ----------
            if (msg.hasAudio() || msg.hasDocument()) {
                handleAudio(msg);
                return;
            }

            // ---------- Список треков ----------
            if (msg.hasText() && msg.getText().equals("/tracks")) {
                sendTrackList(msg.getChatId(), msg.getFrom().getId());
            }
        }

        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
    }

    // ---------------- Mini App кнопка -------------------
private void sendMiniAppButton(Long chatId) {

    InlineKeyboardButton webAppBtn = new InlineKeyboardButton();
    webAppBtn.setText("🎧 Открыть плеер");

    WebAppInfo webAppInfo = new WebAppInfo();
    webAppInfo.setUrl("https://telegram-music-player-uh5o.onrender.com/miniapp?v=2");

    webAppBtn.setWebApp(webAppInfo);

    InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
            List.of(List.of(webAppBtn))
    );

    try {
        execute(
                SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("Открываю плеер 🎵")
                        .replyMarkup(markup)
                        .build()
        );
    } catch (TelegramApiException e) {
        e.printStackTrace();
    }
}
    // ---------------- Обработка загрузки -------------------
    private void handleAudio(Message msg) {

        Long telegramId = msg.getFrom().getId();
        Audio audio = msg.getAudio();
        Document doc = msg.getDocument();

        String fileId = audio != null ? audio.getFileId() : doc.getFileId();

        // Для аудио берём красивое название и исполнителя, а не tmp-файл
        String title = null;
        String performer = null;
        if (audio != null) {
            title = audio.getTitle();
            performer = audio.getPerformer();
        }
        String originalName = title != null && !title.isBlank()
                ? title
                : (audio != null ? audio.getFileName() : doc.getFileName());

        try {
            GetFile getFile = new GetFile(fileId);
            org.telegram.telegrambots.meta.api.objects.File tgFile = execute(getFile);

            String fileUrl =
                    "https://api.telegram.org/file/bot" +
                            config.getBotToken() + "/" +
                            tgFile.getFilePath();

            InputStream input = new URL(fileUrl).openStream();

            String savedName = System.currentTimeMillis() + "_" + (audio != null ? audio.getFileName() : doc.getFileName());
            File localFile = new File("uploads/" + savedName);
            localFile.getParentFile().mkdirs();

            FileOutputStream fos = new FileOutputStream(localFile);
            input.transferTo(fos);
            fos.close();

            trackService.saveTrack(
                    telegramId,
                    originalName,
                    performer,
                    savedName,
                    fileId
            );

            execute(SendMessage.builder()
                    .chatId(msg.getChatId().toString())
                    .text("🎵 Трек сохранён! /tracks чтобы посмотреть список.")
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- Список треков -------------------
    private void sendTrackList(Long chatId, Long telegramId) {
        List<Track> tracks = trackService.getTracksByUser(telegramId);

        if (tracks.isEmpty()) {
            sendText(chatId, "У тебя нет треков пока.");
            return;
        }

        for (Track t : tracks) {

            InlineKeyboardButton play = new InlineKeyboardButton("▶ Прослушать");
            play.setCallbackData("PLAY_" + t.getId());

            InlineKeyboardButton del = new InlineKeyboardButton("❌ Удалить");
            del.setCallbackData("DEL_" + t.getId());

            InlineKeyboardMarkup markup =
                    new InlineKeyboardMarkup(List.of(List.of(play, del)));

            sendText(chatId, "🎧 " + t.getOriginalName(), markup);
        }
    }

    private void sendText(Long chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .build());
        } catch (TelegramApiException ignored) {}
    }

    private void sendText(Long chatId, String text, InlineKeyboardMarkup markup) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .replyMarkup(markup)
                    .build());
        } catch (TelegramApiException ignored) {}
    }

    // ---------------- Callback кнопки -------------------
    private void handleCallback(CallbackQuery cb) {
        String data = cb.getData();
        Long chatId = cb.getMessage().getChatId();

        if (data.startsWith("PLAY_")) {
            Long id = Long.valueOf(data.substring(5));
            playTrack(chatId, id);
        }

        if (data.startsWith("DEL_")) {
            Long id = Long.valueOf(data.substring(4));
            deleteTrack(chatId, id);
        }
    }

    // ---------------- Проигрывание трека -------------------
    private void playTrack(Long chatId, Long trackId) {
        Track t = trackService.getTrack(trackId);

        SendAudio audio = new SendAudio();
        audio.setChatId(chatId.toString());

        audio.setAudio(new InputFile(new File("uploads/" + t.getFilename())));

        try {
            execute(audio);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // ---------------- Удаление -------------------
    private void deleteTrack(Long chatId, Long trackId) {
        trackService.deleteTrack(trackId);
        sendText(chatId, "❌ Трек удалён.");
    }
}

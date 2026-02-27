package com.telegram.musicplayer.controller;

import com.telegram.musicplayer.model.Track;
import com.telegram.musicplayer.service.TrackService;
import com.telegram.musicplayer.service.TelegramAuthService;
import com.telegram.musicplayer.service.TelegramFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;
    private final TelegramAuthService telegramAuthService;
    private final TelegramFileService telegramFileService;

    // ===================== ЗАГРУЗКА (БОТ) =====================
    @PostMapping("/upload")
    public Track uploadTrack(
            @RequestParam Long userId,
            @RequestParam String originalName,
            @RequestParam String filename,
            @RequestParam String telegramFileId
    ) {
        // Для загрузки через REST у нас может не быть информации об исполнителе
        return trackService.saveTrack(userId, originalName, null, filename, telegramFileId);
    }

    // ===================== MINI APP: СПИСОК ТРЕКОВ =====================
  // ===================== MINI APP: СПИСОК ТРЕКОВ =====================
@GetMapping
public List<Track> getTracksForMiniApp(
        @RequestHeader("X-TG-INIT-DATA") String initData
) {
    // просто валидируем Telegram
    // validate and extract user id from initData
    Long userId = telegramAuthService.getUserId(initData);
    // return only user's tracks
    return trackService.getTracksByUser(userId);
}

    // ===================== MINI APP: ПОЛУЧИТЬ URL ДЛЯ ПРОИГРЫВАНИЯ =====================
    @GetMapping("/play/{trackId}")
    public Map<String, String> playTrack(
            @PathVariable Long trackId,
            @RequestHeader("X-TG-INIT-DATA") String initData
    ) {
        // проверяем пользователя
        telegramAuthService.parseAndValidate(initData);

        Track track = trackService.getTrack(trackId);

        // 🔥 ВАЖНО: получаем CDN ссылку Telegram
        String streamUrl = telegramFileService.getFileUrl(track.getTelegramFileId());

        // отдаём ФРОНТУ
        return Map.of("streamUrl", streamUrl);
    }

    // ===================== АДМИН / БОТ =====================
    @GetMapping("/user/{userId}")
    public List<Track> getTracksByUserId(@PathVariable Long userId) {
        return trackService.getTracksByUser(userId);
    }

    // ===================== УДАЛЕНИЕ =====================
    @DeleteMapping("/{trackId}")
    public void deleteTrack(@PathVariable Long trackId) {
        trackService.deleteTrack(trackId);
    }
}

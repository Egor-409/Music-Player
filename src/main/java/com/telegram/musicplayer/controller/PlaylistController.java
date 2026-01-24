package com.telegram.musicplayer.controller;

import com.telegram.musicplayer.model.Playlist;
import com.telegram.musicplayer.service.PlaylistService;
import com.telegram.musicplayer.service.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final TelegramAuthService telegramAuthService;

    /**
     * Получить плейлисты пользователя
     */
    @GetMapping
    public List<Playlist> getPlaylists(
            @RequestHeader("X-TG-INIT-DATA") String initData
    ) {
        Long userId = telegramAuthService.getUserId(initData);
        return playlistService.getUserPlaylists(userId);
    }

    /**
     * Создать плейлист
     */
    @PostMapping
    public Playlist createPlaylist(
            @RequestHeader("X-TG-INIT-DATA") String initData,
            @RequestBody Map<String, String> body
    ) {
        Long userId = telegramAuthService.getUserId(initData);
        String name = body.get("name");
        return playlistService.createPlaylist(userId, name);
    }
}

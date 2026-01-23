package com.telegram.musicplayer.controller;

import com.telegram.musicplayer.model.Playlist;
import com.telegram.musicplayer.model.User;
import com.telegram.musicplayer.repository.PlaylistRepository;
import com.telegram.musicplayer.service.UserService;
import com.telegram.musicplayer.util.TelegramInitDataParser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistRepository playlistRepository;
    private final UserService userService;

    public PlaylistController(PlaylistRepository playlistRepository,
                              UserService userService) {
        this.playlistRepository = playlistRepository;
        this.userService = userService;
    }

    // Получить все плейлисты пользователя
    @GetMapping
    public List<Playlist> getAll(
            @RequestHeader("X-TG-INIT-DATA") String initData) {

        Long telegramId = TelegramInitDataParser.extractUserId(initData);
        User user = userService.findOrCreateUser(telegramId);

        return playlistRepository.findByUserId(user.getId());
    }

    // Создать плейлист
    @PostMapping
    public Playlist create(
            @RequestBody Map<String, String> body,
            @RequestHeader("X-TG-INIT-DATA") String initData) {

        Long telegramId = TelegramInitDataParser.extractUserId(initData);
        User user = userService.findOrCreateUser(telegramId);

        Playlist playlist = new Playlist();
        playlist.setName(body.get("name"));
        playlist.setUser(user);

        return playlistRepository.save(playlist);
    }
}

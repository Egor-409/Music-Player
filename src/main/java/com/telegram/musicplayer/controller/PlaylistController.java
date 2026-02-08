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
@CrossOrigin(origins = "*") // 🔥 важно для ноутбука
public class PlaylistController {

    private final PlaylistService playlistService;
    private final TelegramAuthService telegramAuthService;

    @GetMapping
    public List<Playlist> getPlaylists(
            @RequestHeader("X-TG-INIT-DATA") String initData
    ) {
        Long userId = telegramAuthService.getUserId(initData);
        return playlistService.getUserPlaylists(userId);
    }

    @PostMapping
    public Playlist createPlaylist(
            @RequestHeader("X-TG-INIT-DATA") String initData,
            @RequestBody Map<String, String> body
    ) {
        Long userId = telegramAuthService.getUserId(initData);

        String name = body.get("name");
        return playlistService.createPlaylist(userId, name);
    }

    @GetMapping("/{id}")
    public Playlist getPlaylist(
            @RequestHeader("X-TG-INIT-DATA") String initData,
            @PathVariable Long id
    ) {
        Long userId = telegramAuthService.getUserId(initData);
        return playlistService.getPlaylistWithTracks(userId, id);
    }

    @PostMapping("/{id}/tracks")
    public void addTrackToPlaylist(
            @RequestHeader("X-TG-INIT-DATA") String initData,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Long userId = telegramAuthService.getUserId(initData);
        Object trackIdObj = body.get("trackId");
        if (trackIdObj == null) {
            throw new RuntimeException("trackId is required");
        }
        Long trackId = trackIdObj instanceof Number ? ((Number) trackIdObj).longValue() : Long.parseLong(trackIdObj.toString());
        playlistService.addTrackToPlaylist(userId, id, trackId);
    }

    @DeleteMapping("/{id}/tracks")
    public void removeTrackFromPlaylist(
            @RequestHeader("X-TG-INIT-DATA") String initData,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Long userId = telegramAuthService.getUserId(initData);
        Object trackIdObj = body.get("trackId");
        if (trackIdObj == null) {
            throw new RuntimeException("trackId is required");
        }
        Long trackId = trackIdObj instanceof Number ? ((Number) trackIdObj).longValue() : Long.parseLong(trackIdObj.toString());
        playlistService.removeTrackFromPlaylist(userId, id, trackId);
    }

    @DeleteMapping("/{id}")
    public void deletePlaylist(
            @RequestHeader("X-TG-INIT-DATA") String initData,
            @PathVariable Long id
    ) {
        Long userId = telegramAuthService.getUserId(initData);
        playlistService.deletePlaylist(userId, id);
    }
}

package com.telegram.musicplayer.controller;

import com.telegram.musicplayer.model.Track;
import com.telegram.musicplayer.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    // Бот будет отправлять трек сюда
    @PostMapping("/upload")
    public Track uploadTrack(
            @RequestParam Long userId,
            @RequestParam String originalName,
            @RequestParam String filename,
            @RequestParam String telegramFileId
    ) {
        return trackService.saveTrack(userId, originalName, filename, telegramFileId);
    }
    @GetMapping("/user/{userId}")
public List<Track> getUserTracks(@PathVariable Long userId) {
    return trackService.getTracksByUser(userId);
}

@GetMapping("/user/{telegramUserId}")
public List<Track> getTracksByUser(@PathVariable Long telegramUserId) {
    return trackService.getTracksByUser(telegramUserId);
}

@DeleteMapping("/{trackId}")
public void deleteTrack(@PathVariable Long trackId) {
    trackService.deleteTrack(trackId);
}


    
}

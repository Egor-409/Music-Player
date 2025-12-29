package com.telegram.musicplayer.controller;

import com.telegram.musicplayer.model.Track;
import com.telegram.musicplayer.model.TelegramUser;
import com.telegram.musicplayer.service.TrackService;
import com.telegram.musicplayer.service.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;
    private final TelegramAuthService telegramAuthService;

    // ===================== ЗАГРУЗКА (БОТ) =====================
    @PostMapping("/upload")
    public Track uploadTrack(
            @RequestParam Long userId,
            @RequestParam String originalName,
            @RequestParam String filename,
            @RequestParam String telegramFileId
    ) {
        System.out.println("UPLOAD TRACK: userId=" + userId + ", file=" + originalName);

        return trackService.saveTrack(userId, originalName, filename, telegramFileId);
    }

    // ===================== MINI APP =====================
    @GetMapping
    public List<Track> getTracksForMiniApp(
            @RequestHeader("X-TG-INIT-DATA") String initData
    ) {
        System.out.println("MINI APP REQUEST RECEIVED");
        System.out.println("INIT DATA = " + initData);

        TelegramUser user = telegramAuthService.parseAndValidate(initData);

        System.out.println("MINI APP USER ID = " + user.getId());

        List<Track> tracks = trackService.getTracksByUser(user.getId());

        System.out.println("TRACKS FOUND = " + tracks.size());

        return tracks;
    }

    // ===================== АДМИН / БОТ =====================
    @GetMapping("/user/{userId}")
    public List<Track> getTracksByUserId(@PathVariable Long userId) {
        System.out.println("ADMIN REQUEST USER ID = " + userId);

        return trackService.getTracksByUser(userId);
    }

    // ===================== АУДИО ФАЙЛ =====================
    @GetMapping("/file/{trackId}")
    public ResponseEntity<Resource> getTrackFile(@PathVariable Long trackId) {
        try {
            System.out.println("GET TRACK FILE ID = " + trackId);

            Track track = trackService.getTrack(trackId);
            Path path = trackService.resolveTrackPath(track);

            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                System.out.println("FILE NOT FOUND: " + path);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + track.getOriginalName() + "\""
                    )
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===================== УДАЛЕНИЕ =====================
    @DeleteMapping("/{trackId}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long trackId) {
        System.out.println("DELETE TRACK ID = " + trackId);

        trackService.deleteTrack(trackId);
        return ResponseEntity.noContent().build();
    }
}

package com.telegram.musicplayer.controller;

import com.telegram.musicplayer.model.Track;
import com.telegram.musicplayer.repository.TrackRepository;
import com.telegram.musicplayer.service.TelegramFileService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URL;

@RestController
@RequestMapping("/tracks")
public class TrackStreamController {

    private final TrackRepository trackRepository;
    private final TelegramFileService telegramFileService;

    public TrackStreamController(
            TrackRepository trackRepository,
            TelegramFileService telegramFileService
    ) {
        this.trackRepository = trackRepository;
        this.telegramFileService = telegramFileService;
    }

    @GetMapping("/stream/{trackId}")
    public ResponseEntity<StreamingResponseBody> streamTrack(@PathVariable Long trackId) {

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        String telegramUrl = telegramFileService.getFileUrl(track.getTelegramFileId());

        StreamingResponseBody stream = outputStream -> {
            URL url = new URL(telegramUrl);
            try (InputStream inputStream = url.openStream()) {
                inputStream.transferTo(outputStream);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .body(stream);
    }
}

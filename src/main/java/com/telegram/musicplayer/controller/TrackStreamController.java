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
    public ResponseEntity<Void> stream(@PathVariable Long trackId) {

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        String telegramCdnUrl =
                telegramFileService.getFileUrl(track.getTelegramFileId());

        return ResponseEntity
                .status(302)
                .header(HttpHeaders.LOCATION, telegramCdnUrl)
                .build();
    }
}

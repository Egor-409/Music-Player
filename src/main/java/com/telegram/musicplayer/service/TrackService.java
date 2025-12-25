package com.telegram.musicplayer.service;

import com.telegram.musicplayer.model.Track;
import com.telegram.musicplayer.model.User;
import com.telegram.musicplayer.repository.TrackRepository;
import com.telegram.musicplayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // ---------- СОХРАНЕНИЕ ТРЕКА ----------
    public Track saveTrack(Long telegramUserId, String originalName, String filename, String telegramFileId) {

        User user = userRepository.findById(telegramUserId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(telegramUserId);
                    return userRepository.save(newUser);
                });

        Track track = new Track();
        track.setUser(user);
        track.setOriginalName(originalName);
        track.setFilename(filename);
        track.setTelegramFileId(telegramFileId);

        return trackRepository.save(track);
    }

    // ---------- ПОЛУЧИТЬ ТРЕК ----------
    public Track getTrack(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found"));
    }

    // ---------- ПУТЬ К ФАЙЛУ ----------
    public Path resolveTrackPath(Track track) {
        return Paths.get(uploadDir)
                .resolve(track.getFilename())
                .normalize();
    }

    // ---------- ТРЕКИ ПОЛЬЗОВАТЕЛЯ ----------
    public List<Track> getTracksByUser(Long userId) {
        return trackRepository.findByUserId(userId);
    }

    // ---------- УДАЛЕНИЕ ----------
    public void deleteTrack(Long id) {
        trackRepository.deleteById(id);
    }
}

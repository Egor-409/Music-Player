package com.telegram.musicplayer.service;

import com.telegram.musicplayer.model.Playlist;
import com.telegram.musicplayer.model.Track;
import com.telegram.musicplayer.repository.PlaylistRepository;
import com.telegram.musicplayer.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;

    public List<Playlist> getUserPlaylists(Long userId) {
        return playlistRepository.findByUserId(userId);
    }

    public Playlist getPlaylistWithTracks(Long userId, Long playlistId) {
        Playlist playlist = playlistRepository.findByIdWithTracks(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        return playlist;
    }

    public Playlist createPlaylist(Long userId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Playlist name is empty");
        }

        Playlist playlist = new Playlist();
        playlist.setName(name.trim());
        playlist.setUserId(userId);

        return playlistRepository.save(playlist);
    }

    public void deletePlaylist(Long userId, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        playlistRepository.delete(playlist);
    }

    public void addTrackToPlaylist(Long userId, Long playlistId, Long trackId) {
        Playlist playlist = playlistRepository.findByIdWithTracks(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));
        if (!track.getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        if (playlist.getTracks().stream().noneMatch(t -> t.getId().equals(trackId))) {
            playlist.getTracks().add(track);
            playlistRepository.save(playlist);
        }
    }

    public void removeTrackFromPlaylist(Long userId, Long playlistId, Long trackId) {
        Playlist playlist = playlistRepository.findByIdWithTracks(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        boolean removed = playlist.getTracks().removeIf(t -> t.getId().equals(trackId));
        if (removed) {
            playlistRepository.save(playlist);
        }
    }
}

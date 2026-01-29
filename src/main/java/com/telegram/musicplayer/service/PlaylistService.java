package com.telegram.musicplayer.service;

import com.telegram.musicplayer.model.Playlist;
import com.telegram.musicplayer.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public List<Playlist> getUserPlaylists(Long userId) {
        return playlistRepository.findByUserId(userId);
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
}

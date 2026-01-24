package com.telegram.musicplayer.service;

import com.telegram.musicplayer.model.Playlist;
import com.telegram.musicplayer.model.Track;
import com.telegram.musicplayer.model.User;
import com.telegram.musicplayer.repository.PlaylistRepository;
import com.telegram.musicplayer.repository.TrackRepository;
import com.telegram.musicplayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    /**
     * Создать плейлист
     */
    public Playlist createPlaylist(Long telegramUserId, String name) {
        User user = userRepository.findById(telegramUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setUser(user);

        return playlistRepository.save(playlist);
    }

    /**
     * Получить плейлисты пользователя
     */
    @Transactional(readOnly = true)
    public List<Playlist> getUserPlaylists(Long telegramUserId) {
        return playlistRepository.findByUserId(telegramUserId);
    }

    /**
     * Добавить трек в плейлист
     */
    public void addTrackToPlaylist(Long telegramUserId, Long playlistId, Long trackId) {
        Playlist playlist = getUserPlaylist(telegramUserId, playlistId);
        Track track = getUserTrack(telegramUserId, trackId);

        if (!playlist.getTracks().contains(track)) {
            playlist.getTracks().add(track);
        }
    }

    /**
     * Получить треки плейлиста
     */
    @Transactional(readOnly = true)
    public List<Track> getPlaylistTracks(Long telegramUserId, Long playlistId) {
        Playlist playlist = getUserPlaylist(telegramUserId, playlistId);
        return playlist.getTracks();
    }

    /**
     * Удалить трек из плейлиста
     */
    public void removeTrackFromPlaylist(Long telegramUserId, Long playlistId, Long trackId) {
        Playlist playlist = getUserPlaylist(telegramUserId, playlistId);
        playlist.getTracks().removeIf(track -> track.getId().equals(trackId));
    }

    // ------------------ helpers ------------------

    private Playlist getUserPlaylist(Long telegramUserId, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getId().equals(telegramUserId)) {
            throw new RuntimeException("Access denied");
        }

        return playlist;
    }

    private Track getUserTrack(Long telegramUserId, Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        if (!track.getUser().getId().equals(telegramUserId)) {
            throw new RuntimeException("Access denied");
        }

        return track;
    }
}

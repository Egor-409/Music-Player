package com.telegram.musicplayer.repository;

import com.telegram.musicplayer.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}

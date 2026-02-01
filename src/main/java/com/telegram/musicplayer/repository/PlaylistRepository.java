package com.telegram.musicplayer.repository;

import com.telegram.musicplayer.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findByUserId(Long userId);

    @Query("SELECT p FROM Playlist p LEFT JOIN FETCH p.tracks WHERE p.id = :id")
    Optional<Playlist> findByIdWithTracks(@Param("id") Long id);
}

package com.telegram.musicplayer.repository;

import com.telegram.musicplayer.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {

    // ВАЖНО: именно User_Id, а не UserId
    List<Track> findByUser_Id(Long userId);

}



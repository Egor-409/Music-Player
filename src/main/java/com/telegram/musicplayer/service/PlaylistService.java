package com.telegram.musicplayer.service;

import com.telegram.musicplayer.model.Playlist;
import com.telegram.musicplayer.repository.PlaylistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistService {

    private final PlaylistRepository repository;

    public PlaylistService(PlaylistRepository repository) {
        this.repository = repository;
    }

    public List<Playlist> getAll() {
        return repository.findAll();
    }

    public Playlist create(String name) {
        return repository.save(new Playlist(name));
    }
}

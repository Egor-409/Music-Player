package com.telegram.musicplayer.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // на будущее
    // @ManyToMany
    // private List<Track> tracks;

    public Playlist() {}

    public Playlist(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}

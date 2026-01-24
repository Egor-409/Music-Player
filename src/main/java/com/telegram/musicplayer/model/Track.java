package com.telegram.musicplayer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tracks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название файла на сервере
    private String filename;

    // Исходное название файла
    private String originalName;

    // file_id Telegram
    private String telegramFileId;

    // Telegram user id
    @Column(name = "user_id", nullable = false)
    private Long userId;
}

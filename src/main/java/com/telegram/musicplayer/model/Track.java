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

    // Исходное название файла (как прислал пользователь)
    private String originalName;

    // file_id из Telegram, чтобы можно было скачивать
    private String telegramFileId;

    // Связь с пользователем (много треков → один пользователь)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

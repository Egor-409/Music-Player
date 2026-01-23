package com.telegram.musicplayer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    // Telegram User ID → Long
    private Long id;

    private String username;

    private String firstName;

    private String lastName;
}

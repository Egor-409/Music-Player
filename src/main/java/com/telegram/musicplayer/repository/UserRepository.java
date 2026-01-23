package com.telegram.musicplayer.repository;

import com.telegram.musicplayer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

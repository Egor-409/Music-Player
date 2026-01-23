package com.telegram.musicplayer.service;

import com.telegram.musicplayer.model.User;
import com.telegram.musicplayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOrCreateUser(Long telegramId) {
        return userRepository.findById(telegramId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setId(telegramId);
                    return userRepository.save(user);
                });
    }

    public User getUser(Long telegramId) {
        return userRepository.findById(telegramId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
}

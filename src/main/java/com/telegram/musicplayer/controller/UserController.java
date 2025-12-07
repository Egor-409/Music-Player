package com.telegram.musicplayer.controller;

import com.telegram.musicplayer.model.User;
import com.telegram.musicplayer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth")
    public User authUser(@RequestParam Long telegramId) {
        return userService.findOrCreateUser(telegramId);
    }
}

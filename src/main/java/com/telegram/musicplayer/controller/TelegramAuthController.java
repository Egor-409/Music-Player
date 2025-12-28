package com.telegram.musicplayer.controller;

import com.telegram.musicplayer.model.TelegramUser;
import com.telegram.musicplayer.service.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TelegramAuthController {

    private final TelegramAuthService authService;

    @PostMapping("/telegram")
    public TelegramUser auth(@RequestBody String initData) {
        return authService.parseAndValidate(initData);
    }
}

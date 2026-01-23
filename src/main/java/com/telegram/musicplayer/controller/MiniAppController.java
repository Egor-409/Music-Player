package com.telegram.musicplayer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MiniAppController {

    @GetMapping("/miniapp")
    public String miniApp() {
        // forward вместо redirect
        return "forward:/miniapp/index.html";
    }
}

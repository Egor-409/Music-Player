package com.telegram.musicplayer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * Отключает кэш для Mini App, чтобы на телефоне всегда подгружалась свежая версия.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/miniapp/**")
                .addResourceLocations("classpath:/static/miniapp/")
                .setCacheControl(CacheControl.noStore().mustRevalidate());
    }
}

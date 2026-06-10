package ru.find.me.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class MvcConfigurer implements WebMvcConfigurer {
    @Value("${upload.path}")
    private String uploadPath;

    // Отдаёт загруженные изображения (аватары, файлы публикаций) из upload.path.
    // В проде nginx проксирует /img/ сюда. View-контроллеры и /static/** удалены
    // вместе с FreeMarker — статику теперь раздаёт фронтенд (React/Vite).
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + uploadPath + File.separator);
    }
}
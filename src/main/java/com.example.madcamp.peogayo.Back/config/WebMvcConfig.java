package com.example.madcamp.peogayo.Back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rootPath = System.getProperty("user.dir");

        // 경로 구분자 문제 해결을 위한 File 객체 활용
        File uploadDir = new File(rootPath, "uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 'file:' 접두사 뒤에 절대경로를 붙임
        String uploadPath = "file:" + uploadDir.getAbsolutePath() + "/";

        System.out.println("이미지 서빙 경로: " + uploadPath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*");
    }
}
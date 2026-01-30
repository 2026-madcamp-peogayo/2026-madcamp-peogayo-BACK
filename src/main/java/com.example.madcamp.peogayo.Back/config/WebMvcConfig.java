package com.example.madcamp.peogayo.Back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 1. 이미지 파일 접근 경로 설정
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 내 프로젝트 폴더 안의 /uploads/ 폴더에 있는 파일을 보여줌
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
    }

    // 2. 외부 접속 허용 설정 (CORS 추가됨)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")      // 모든 API 경로에 대해
                .allowedOrigins("*")    // 모든 IP 주소에서의 접속을 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*");
    }
}
package com.example.madcamp.peogayo.Back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. 암호화 도구 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 보안 필터 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // (1) CSRF, 로그인 폼, HTTP Basic 인증 모두 끄기 (로그인 창 안 뜨게)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // (2) 요청 주소별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 1. Swagger 관련 주소 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

                        // 2. 회원가입, 로그인 API 경로는 허용
                        .requestMatchers("/api/users/**").permitAll()

                        // 3. 이미지 파일 경로 허용
                        .requestMatchers("/uploads/**").permitAll()

                        // 4. 나머지 모든 요청도 허용
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
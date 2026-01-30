package com.example.madcamp.peogayo.Back.dto;

import com.example.madcamp.peogayo.Back.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HomeResponseDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String bgImageUrl;
    private String greeting; // 인사말
    private boolean isMyHome; // 내가 방문한 홈인지 여부

    // User 엔티티를 받아서 DTO로 변환하는 생성자
    public static HomeResponseDto from(User user, boolean isMyHome) {
        HomeResponseDto dto = new HomeResponseDto();
        dto.setUserId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setBgImageUrl(user.getBgImageUrl());
        dto.setGreeting(user.getGreeting());
        dto.setIsMyHome(isMyHome);
        return dto;
    }
}
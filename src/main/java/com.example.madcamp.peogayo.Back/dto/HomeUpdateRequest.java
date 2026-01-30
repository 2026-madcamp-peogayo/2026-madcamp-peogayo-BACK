package com.example.madcamp.peogayo.Back.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HomeUpdateRequest {
    private String nickname;        // 닉네임 변경
    private String greeting;        // 인사말 변경
    private String profileImageUrl; // 프로필 사진 URL
    private String bgImageUrl;      // 배경 사진 URL

    // 비공개 설정
    private Boolean isHomePrivate;       // 마이홈 비공개 여부
    private Boolean isFriendListPrivate; // 친구목록 비공개 여부
}

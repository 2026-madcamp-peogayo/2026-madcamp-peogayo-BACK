package com.example.madcamp.peogayo.Back.dto;

import com.example.madcamp.peogayo.Back.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FriendDto {
    private Long userId;       // 친구의 ID (클릭 시 이동용)
    private String nickname;   // 친구 닉네임
    private String profileImg; // 친구 프사

    public static FriendDto from(User user) {
        FriendDto dto = new FriendDto();
        dto.setUserId(user.getId());
        dto.setNickname(user.getNickname());
        dto.setProfileImg(user.getProfileImageUrl());
        return dto;
    }
}
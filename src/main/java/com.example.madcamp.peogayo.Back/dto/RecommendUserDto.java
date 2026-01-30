package com.example.madcamp.peogayo.Back.dto;

import com.example.madcamp.peogayo.Back.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendUserDto {
    private Long userId;
    private String nickname;
    private String profileImg;

    public static RecommendUserDto from(User user) {
        return RecommendUserDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImageUrl())
                .build();
    }
}
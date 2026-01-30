package com.example.madcamp.peogayo.Back.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeUserDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
}
package com.example.madcamp.peogayo.Back.dto;

import com.example.madcamp.peogayo.Back.entity.Guestbook;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class GuestbookResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    // 작성자 정보
    private Long writerId;
    private String writerNickname;
    private String writerProfileImg;

    public static GuestbookResponseDto from(Guestbook gb) {
        GuestbookResponseDto dto = new GuestbookResponseDto();
        dto.setId(gb.getId());
        dto.setContent(gb.getContent());
        dto.setCreatedAt(gb.getCreatedAt());

        if (gb.getWriter() != null) {
            dto.setWriterId(gb.getWriter().getId());
            dto.setWriterNickname(gb.getWriter().getNickname());
            dto.setWriterProfileImg(gb.getWriter().getProfileImageUrl());
        }
        return dto;
    }
}
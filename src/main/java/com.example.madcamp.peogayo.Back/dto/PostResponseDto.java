package com.example.madcamp.peogayo.Back.dto;

import com.example.madcamp.peogayo.Back.entity.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String contentText;
    private String contentImageUrl;
    private Post.Visibility visibility;
    private LocalDateTime createdAt;

    // 작성자 정보
    private Long writerId;
    private String writerNickname;
    private String writerProfileImg;

    public static PostResponseDto from(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());
        dto.setContentText(post.getContentText());
        dto.setContentImageUrl(post.getContentImageUrl());
        dto.setVisibility(post.getVisibility());
        dto.setCreatedAt(post.getCreatedAt());

        if (post.getWriter() != null) {
            dto.setWriterId(post.getWriter().getId());
            dto.setWriterNickname(post.getWriter().getNickname());
            dto.setWriterProfileImg(post.getWriter().getProfileImageUrl());
        }
        return dto;
    }
}
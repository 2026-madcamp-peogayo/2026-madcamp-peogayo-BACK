package com.example.madcamp.peogayo.Back.dto;

import com.example.madcamp.peogayo.Back.entity.Post;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostCreateRequest {
    private String contentText;      // 글 내용
    private String contentImageUrl;  // 사진 URL
    private Post.Visibility visibility; // 공개 범위 (PUBLIC, FRIENDS, PRIVATE)
}
package com.example.madcamp.peogayo.Back.dto;

import com.example.madcamp.peogayo.Back.entity.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostCreateDto {
    private String contentText;
    private Post.Visibility visibility;
}
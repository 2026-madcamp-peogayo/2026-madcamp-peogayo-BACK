package com.example.madcamp.peogayo.Back.service;

import com.example.madcamp.peogayo.Back.dto.LikeUserDto;
import com.example.madcamp.peogayo.Back.entity.Post;
import com.example.madcamp.peogayo.Back.entity.PostLike;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.repository.PostLikeRepository;
import com.example.madcamp.peogayo.Back.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    // 1. 좋아요 토글 (누르면 ON, 다시 누르면 OFF)
    @Transactional
    public boolean toggleLike(User user, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        Optional<PostLike> existingLike = postLikeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            // 이미 있으면 -> 삭제 (좋아요 취소)
            postLikeRepository.delete(existingLike.get());
            return false;
        } else {
            // 없으면 -> 추가 (좋아요)
            postLikeRepository.save(new PostLike(user, post));
            return true;
        }
    }

    // 2. 좋아요 누른 사람 목록 조회 (게시글 작성자만 가능)
    public List<LikeUserDto> getLikers(User loginUser, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        if (!post.getWriter().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("작성자만 좋아요 목록을 볼 수 있습니다.");
        }

        List<PostLike> likes = postLikeRepository.findAllByPostId(postId);

        return likes.stream()
                .map(like -> LikeUserDto.builder()
                        .userId(like.getUser().getId())
                        .nickname(like.getUser().getNickname())
                        .profileImageUrl(like.getUser().getProfileImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
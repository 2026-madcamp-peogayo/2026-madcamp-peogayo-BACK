package com.example.madcamp.peogayo.Back.repository;

import com.example.madcamp.peogayo.Back.entity.Post;
import com.example.madcamp.peogayo.Back.entity.PostLike;
import com.example.madcamp.peogayo.Back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // 이미 좋아요를 눌렀는지 확인
    boolean existsByUserAndPost(User user, Post post);

    // 좋아요 취소를 위해 찾기
    Optional<PostLike> findByUserAndPost(User user, Post post);

    // 특정 게시글의 좋아요 목록 가져오기
    List<PostLike> findAllByPostId(Long postId);

    // 특정 게시글의 좋아요 개수 세기
    int countByPostId(Long postId);
}
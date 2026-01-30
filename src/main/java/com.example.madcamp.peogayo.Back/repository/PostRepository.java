package com.example.madcamp.peogayo.Back.repository;

import com.example.madcamp.peogayo.Back.entity.Post;
import com.example.madcamp.peogayo.Back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 특정 유저가 쓴 글을 최신순으로 가져오기
    List<Post> findAllByWriterIdOrderByCreatedAtDesc(Long writerId);

    // MAIN-01: 전체 글 보기
    @Query("SELECT p FROM Post p JOIN p.writer u " +
            "WHERE p.visibility = 'PUBLIC' AND u.isHomePrivate = false " +
            "ORDER BY p.createdAt DESC")
    List<Post> findAllPublicPosts();

    // MAIN-02: 친구 글 보기
    @Query("SELECT p FROM Post p WHERE p.writer.id IN :friendIds ORDER BY p.createdAt DESC")
    List<Post> findPostsByFriendIds(@Param("friendIds") List<Long> friendIds);
}
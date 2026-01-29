package com.example.madcamp.peogayo.Back.repositiory;

import com.example.madcamp.peogayo.Back.entity.Post;
import com.example.madcamp.peogayo.Back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 특정 유저가 쓴 글을 최신순으로 가져오기
    List<Post> findAllByWriterOrderByCreatedAtDesc(User writer);
}
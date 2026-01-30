package com.example.madcamp.peogayo.Back.controller;

import com.example.madcamp.peogayo.Back.dto.LikeUserDto;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "좋아요 (Like)", description = "게시글 좋아요 및 좋아요 목록 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class LikeController {

    private final LikeService likeService;

    // 1. 좋아요 누르기/취소하기 (토글)
    @Operation(summary = "좋아요 토글", description = "게시글에 좋아요를 누르거나 취소합니다.")
    @PostMapping("/{postId}/like")
    public ResponseEntity<Boolean> toggleLike(@PathVariable Long postId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User loginUser = (User) session.getAttribute("loginUser");

        boolean isLiked = likeService.toggleLike(loginUser, postId);
        return ResponseEntity.ok(isLiked);
    }

    // 2. 좋아요 누른 사람 목록 조회 (내 글일 때만 가능)
    @Operation(summary = "좋아요 목록 조회", description = "내 게시글에 좋아요 누른 사람 목록을 봅니다. (작성자 본인만 가능)")
    @GetMapping("/{postId}/likes")
    public ResponseEntity<?> getLikers(@PathVariable Long postId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        User loginUser = (User) session.getAttribute("loginUser");

        try {
            List<LikeUserDto> likers = likeService.getLikers(loginUser, postId);
            return ResponseEntity.ok(likers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
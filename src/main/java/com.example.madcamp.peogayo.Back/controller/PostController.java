package com.example.madcamp.peogayo.Back.controller;

import com.example.madcamp.peogayo.Back.dto.PostCreateRequest;
import com.example.madcamp.peogayo.Back.dto.PostResponseDto;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "2. 타임라인 (Post)", description = "게시글 작성, 조회, 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 1. 게시글 작성
    // POST /api/posts
    @Operation(summary = "게시글 작성", description = "내 홈피 타임라인에 새 글을 씁니다. (공개범위: PUBLIC, FRIENDS, PRIVATE)")
    @PostMapping
    public ResponseEntity<String> writePost(@RequestBody PostCreateRequest request, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        User loginUser = (User) session.getAttribute("loginUser");

        postService.writePost(loginUser, request);
        return ResponseEntity.ok("게시글이 작성되었습니다.");
    }

    // 2. 특정 유저의 게시글 목록 조회
    // GET /api/posts/{targetUserId}
    @Operation(summary = "특정 유저의 게시글 목록 조회", description = "주인, 친구, 타인 여부에 따라 공개된 글만 필터링해서 보여줍니다.")
    @GetMapping("/{targetUserId}")
    public ResponseEntity<List<PostResponseDto>> getPostList(@PathVariable Long targetUserId, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute("loginUser") : null;

        List<PostResponseDto> list = postService.getPostList(targetUserId, loginUser);
        return ResponseEntity.ok(list);
    }

    // 3. 게시글 삭제
    // DELETE /api/posts/{postId}
    @Operation(summary = "게시글 삭제", description = "글쓴이 본인만 삭제할 수 있습니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        User loginUser = (User) session.getAttribute("loginUser");

        try {
            postService.deletePost(postId, loginUser);
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
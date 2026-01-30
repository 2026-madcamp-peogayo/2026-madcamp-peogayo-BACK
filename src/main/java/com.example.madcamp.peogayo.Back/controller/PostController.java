package com.example.madcamp.peogayo.Back.controller;

import com.example.madcamp.peogayo.Back.dto.PostCreateDto; // DTO 변경됨
import com.example.madcamp.peogayo.Back.dto.PostResponseDto;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // 추가됨
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // 추가됨

import java.io.IOException;
import java.util.List;

@Tag(name = "타임라인 (Post)", description = "게시글 작성(이미지), 조회, 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 1. 게시글 작성 (이미지 업로드 필수)
    @Operation(summary = "게시글 작성", description = "그림판 이미지 파일을 포함한 게시글 정보를 함께 업로드합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestPart(value = "data") PostCreateDto requestDto,
            @RequestPart(value = "image") MultipartFile image,
            HttpServletRequest httpRequest
    ) {
        // 1. 로그인 체크
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        User loginUser = (User) session.getAttribute("loginUser");

        try {
            // 2. 서비스 호출 (이미지 + 데이터 전달)
            PostResponseDto response = postService.createPost(loginUser, requestDto, image);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 저장 중 오류가 발생했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. 특정 유저의 게시글 목록 조회
    @Operation(summary = "특정 유저의 게시글 목록 조회", description = "주인, 친구, 타인 여부에 따라 공개된 글만 필터링해서 보여줍니다.")
    @GetMapping("/{targetUserId}")
    public ResponseEntity<List<PostResponseDto>> getPostList(@PathVariable Long targetUserId, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute("loginUser") : null;

        List<PostResponseDto> list = postService.getPostList(targetUserId, loginUser);
        return ResponseEntity.ok(list);
    }

    // 3. 게시글 삭제
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
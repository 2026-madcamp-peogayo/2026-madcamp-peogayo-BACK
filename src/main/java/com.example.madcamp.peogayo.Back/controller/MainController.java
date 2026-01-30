package com.example.madcamp.peogayo.Back.controller;

import com.example.madcamp.peogayo.Back.dto.PostResponseDto;
import com.example.madcamp.peogayo.Back.dto.RecommendUserDto;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "광장 (Main)", description = "메인 화면, 파도타기, 전체 글/친구 글 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainController {

    private final MainService mainService;

    // MAIN-01: 전체 글 보기 (비로그인도 가능)
    @Operation(summary = "전체 글 보기 (추천 글)", description = "홈이 공개된 모든 유저의 공개 게시글을 최신순으로 봅니다.")
    @GetMapping("/posts/all")
    public ResponseEntity<List<PostResponseDto>> getAllPublicPosts() {
        List<PostResponseDto> list = mainService.getAllPublicPosts();
        return ResponseEntity.ok(list);
    }

    // MAIN-02: 친구 글 보기 (로그인 필수)
    @Operation(summary = "친구 글 보기", description = "내 일촌들의 게시글만 모아서 봅니다. (로그인 필요)")
    @GetMapping("/posts/friends")
    public ResponseEntity<List<PostResponseDto>> getFriendPosts(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            // 친구 글은 로그인 안하면 못 봄 -> 401 에러
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User loginUser = (User) session.getAttribute("loginUser");

        List<PostResponseDto> list = mainService.getFriendPosts(loginUser);
        return ResponseEntity.ok(list);
    }

    // MAIN-04: 파도타기 (랜덤 유저 ID 반환)
    @Operation(summary = "파도타기", description = "나를 제외한 공개 유저 중 1명을 랜덤 추첨하여 ID를 반환 (프론트에서 해당 ID로 이동해야함)")
    @GetMapping("/surfing")
    public ResponseEntity<?> surfing(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        Long myId = 0L;
        if (session != null && session.getAttribute("loginUser") != null) {
            User loginUser = (User) session.getAttribute("loginUser");
            myId = loginUser.getId();
        }

        try {
            Long randomUserId = mainService.getRandomUserForSurfing(myId);
            return ResponseEntity.ok(randomUserId);
        } catch (IllegalArgumentException e) {
            // service에서 던진 에러 메시지를 그대로 프론트에 전달 (400 Bad Request)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // MAIN-05: 오늘의 추천 (랜덤 5명)
    @Operation(summary = "오늘의 추천 친구", description = "공개 유저 5명을 랜덤으로 추천해줍니다.")
    @GetMapping("/recommend")
    public ResponseEntity<List<RecommendUserDto>> getTodayRecommendations(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        Long myId = 0L;
        if (session != null && session.getAttribute("loginUser") != null) {
            User loginUser = (User) session.getAttribute("loginUser");
            myId = loginUser.getId();
        }

        List<RecommendUserDto> list = mainService.getTodayRecommendations(myId);
        return ResponseEntity.ok(list);
    }
}
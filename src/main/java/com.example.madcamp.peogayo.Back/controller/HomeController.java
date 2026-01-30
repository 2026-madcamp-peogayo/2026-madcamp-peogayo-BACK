package com.example.madcamp.peogayo.Back.controller;

import com.example.madcamp.peogayo.Back.dto.HomeResponseDto;
import com.example.madcamp.peogayo.Back.dto.HomeUpdateRequest;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.service.HomeService;
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

import java.io.IOException; // 추가됨

@Tag(name = "마이홈 (Home)", description = "홈피 정보 조회, 프로필 수정 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    // 마이홈 정보 조회
    @Operation(summary = "마이홈 정보 조회", description = "특정 유저의 프로필, 배경, 인사말 등을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<HomeResponseDto> getHomeInfo(@PathVariable Long userId, HttpServletRequest request) {

        // 세션에서 로그인한 사람 정보 가져오기 (없으면 null)
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute("loginUser") : null;

        HomeResponseDto homeInfo = homeService.getHomeInfo(userId, loginUser);

        return ResponseEntity.ok(homeInfo);
    }

    // 프로필 수정
    @Operation(summary = "프로필 수정", description = "내 홈피의 닉네임, 프사, 비공개 설정 등을 수정합니다.")
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProfile(
            @RequestPart(value = "data") HomeUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "bgImage", required = false) MultipartFile bgImage,
            HttpServletRequest httpRequest) {

        // 1. 로그인 체크
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 2. 세션에서 내 정보 꺼내기
        User loginUser = (User) session.getAttribute("loginUser");

        try {
            // 3. 수정 요청 (파일 포함하여 서비스 호출)
            User updatedUser = homeService.updateHomeProfile(loginUser, request, profileImage, bgImage);

            // 4. 세션 정보 갱신
            session.setAttribute("loginUser", updatedUser);

            return ResponseEntity.ok("프로필이 수정되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 저장 중 오류가 발생했습니다.");
        }
    }
}
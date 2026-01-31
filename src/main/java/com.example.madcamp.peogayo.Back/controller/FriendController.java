package com.example.madcamp.peogayo.Back.controller;

import com.example.madcamp.peogayo.Back.dto.FriendDto;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.repository.UserRepository;
import com.example.madcamp.peogayo.Back.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "친구 (Friend)", description = "일촌 목록 조회, 신청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserRepository userRepository;

    // 친구 목록 조회 (일촌 목록)
    @Operation(summary = "친구 목록 조회", description = "특정 유저의 일촌 목록을 조회합니다. (비공개 설정 시 타인은 조회 불가)")
    @GetMapping("/{targetUserId}")
    public ResponseEntity<List<FriendDto>> getFriendList(@PathVariable Long targetUserId, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute("loginUser") : null;

        List<FriendDto> friends = friendService.getFriendList(targetUserId, loginUser);
        return ResponseEntity.ok(friends);
    }

    // 2. 친구 신청 보내기
    @Operation(summary = "일촌 신청 보내기", description = "다른 유저에게 일촌 신청을 보냅니다. (이미 친구거나 신청 중이면 실패)")
    @PostMapping("/request/{receiverId}")
    public ResponseEntity<String> requestFriend(@PathVariable Long receiverId, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        User loginUser = (User) session.getAttribute("loginUser");

        try {
            friendService.addFriend(loginUser, receiverId);
            return ResponseEntity.ok("일촌 맺기가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
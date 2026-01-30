package com.example.madcamp.peogayo.Back.controller;

import com.example.madcamp.peogayo.Back.dto.FriendDto;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.repository.UserRepository;
import com.example.madcamp.peogayo.Back.service.FriendService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserRepository userRepository;

    // 친구 목록 조회 (일촌 목록)
    @GetMapping("/{targetUserId}")
    public ResponseEntity<List<FriendDto>> getFriendList(@PathVariable Long targetUserId, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute("loginUser") : null;

        List<FriendDto> friends = friendService.getFriendList(targetUserId, loginUser);
        return ResponseEntity.ok(friends);
    }

    // 2. 친구 신청 보내기
    @PostMapping("/request/{receiverId}")
    public ResponseEntity<String> requestFriend(@PathVariable Long receiverId, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        User loginUser = (User) session.getAttribute("loginUser");

        try {
            friendService.requestFriend(loginUser, receiverId);
            return ResponseEntity.ok("일촌 신청을 보냈습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. 친구 신청 수락하기
    @PostMapping("/accept/{requesterId}")
    public ResponseEntity<String> acceptFriend(@PathVariable Long requesterId, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        User loginUser = (User) session.getAttribute("loginUser");

        try {
            friendService.acceptFriend(loginUser, requesterId);
            return ResponseEntity.ok("일촌 신청을 수락했습니다!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
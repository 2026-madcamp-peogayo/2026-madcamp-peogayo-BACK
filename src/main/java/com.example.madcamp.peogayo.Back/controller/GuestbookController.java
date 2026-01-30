package com.example.madcamp.peogayo.Back.controller;

import com.example.madcamp.peogayo.Back.dto.GuestbookRequest;
import com.example.madcamp.peogayo.Back.dto.GuestbookResponseDto;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.service.GuestbookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guestbooks")
public class GuestbookController {

    private final GuestbookService guestbookService;

    // 1. 방명록 작성 - ownerId: 방명록 주인
    @PostMapping("/{ownerId}")
    public ResponseEntity<String> writeGuestbook(@PathVariable Long ownerId,
                                                 @RequestBody GuestbookRequest request,
                                                 HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        User loginUser = (User) session.getAttribute("loginUser");

        guestbookService.writeGuestbook(ownerId, loginUser, request);
        return ResponseEntity.ok("방명록이 작성되었습니다.");
    }

    // 2. 방명록 목록 조회
    @GetMapping("/{ownerId}")
    public ResponseEntity<List<GuestbookResponseDto>> getGuestbookList(@PathVariable Long ownerId) {
        List<GuestbookResponseDto> list = guestbookService.getGuestbookList(ownerId);
        return ResponseEntity.ok(list);
    }

    // 3. 방명록 삭제
    @DeleteMapping("/{guestbookId}")
    public ResponseEntity<String> deleteGuestbook(@PathVariable Long guestbookId, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        User loginUser = (User) session.getAttribute("loginUser");

        try {
            guestbookService.deleteGuestbook(guestbookId, loginUser);
            return ResponseEntity.ok("방명록이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
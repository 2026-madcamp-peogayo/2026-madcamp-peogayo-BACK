package com.example.madcamp.peogayo.Back.controller;

import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.dto.LoginRequest;
import com.example.madcamp.peogayo.Back.dto.SignupRequest;
import com.example.madcamp.peogayo.Back.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 1. 아이디 중복 확인 (회원가입 버튼 누르기 전 체크)
    @GetMapping("/check-loginid")
    public ResponseEntity<Boolean> checkLoginId(@RequestParam String loginId) {
        boolean isDuplicate = userService.checkLoginIdDuplicate(loginId);
        // true면 사용불가, false면 사용가능
        return ResponseEntity.ok(isDuplicate);
    }

    // 2. 닉네임 중복 확인 (회원가입 버튼 누르기 전 체크)
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean isDuplicate = userService.checkNicknameDuplicate(nickname);
        // true면 사용불가, false면 사용가능
        return ResponseEntity.ok(isDuplicate);
    }

    // 3. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        try {
            userService.signup(request);
            return ResponseEntity.ok("회원가입 성공!");
        } catch (IllegalArgumentException e) {
            // Service에서 던진 에러 메시지(비번 길이, 중복 등)를 그대로 프론트엔드에 전달
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 4. 로그인 (세션 생성)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        User user = userService.login(request.getLoginId(), request.getPassword());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 틀렸습니다.");
        }

        // 로그인 성공 시 세션(Session) 생성
        HttpSession session = httpRequest.getSession();
        session.setAttribute("loginUser", user);
        session.setMaxInactiveInterval(1800);

        return ResponseEntity.ok("로그인 성공! (세션 생성됨)");
    }

    // 5. 로그아웃 (세션 삭제)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    // 6. 현재 로그인한 내 정보 확인
    @GetMapping("/me")
    public ResponseEntity<?> checkLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 안 됨");
        }

        // 세션에서 꺼내서 돌려주기
        User loginUser = (User) session.getAttribute("loginUser");
        return ResponseEntity.ok(loginUser);
    }
}
package com.example.madcamp.peogayo.Back.service;

import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.dto.SignupRequest;
import com.example.madcamp.peogayo.Back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. 아이디 중복 확인
    public boolean checkLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    // 2. 닉네임 중복 확인
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 3. 회원가입
    public User signup(SignupRequest request) {
        // [검증 1] 아이디 중복 체크
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // [검증 2] 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // [검증 3] 비밀번호 길이 체크 (8자 이상)
        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        // [검증 4] 비밀번호 재확인 일치 여부
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 모든 검증 통과 -> DB에 저장
        User user = new User();
        user.setLoginId(request.getLoginId());

        // 비밀번호 암호화해서 저장
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        user.setNickname(request.getNickname());

        return userRepository.save(user);
    }

    // 4. 로그인
    public User login(String loginId, String password) {
        Optional<User> optionalUser = userRepository.findByLoginId(loginId);

        // 아이디가 없으면 null 반환
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // matches(입력한비번, 암호화된비번)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        return user;
    }
}
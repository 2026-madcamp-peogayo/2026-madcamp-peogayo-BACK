package com.example.madcamp.peogayo.Back.repository;

import com.example.madcamp.peogayo.Back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId); // 로그인용
    boolean existsByLoginId(String loginId); // 아이디 중복용
    boolean existsByNickname(String nickname);// 닉네임 중복 체크
}
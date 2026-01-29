package com.example.madcamp.peogayo.Back.repositiory;

import com.example.madcamp.peogayo.Back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId); // 로그인용
    boolean existsByLoginId(String loginId);      // 아이디 중복 체크
}
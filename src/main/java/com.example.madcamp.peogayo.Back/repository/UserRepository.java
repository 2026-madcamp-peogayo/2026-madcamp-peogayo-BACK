package com.example.madcamp.peogayo.Back.repository;

import com.example.madcamp.peogayo.Back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId); // 로그인용
    boolean existsByLoginId(String loginId); // 아이디 중복용
    boolean existsByNickname(String nickname);// 닉네임 중복 체크

    // 랜덤 공용 유저 조회 (본인 제외)
    @Query(value = "SELECT * FROM user WHERE is_home_private = false AND id <> :myId ORDER BY RAND() LIMIT :limitCnt", nativeQuery = true)
    List<User> findRandomPublicUsers(@Param("myId") Long myId, @Param("limitCnt") int limitCnt);
}
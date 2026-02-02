package com.example.madcamp.peogayo.Back.repository;

import com.example.madcamp.peogayo.Back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);

    @Query(value = "SELECT * FROM users WHERE is_home_private = 0 AND id <> :myId ORDER BY RAND() LIMIT :limitCnt", nativeQuery = true)
    List<User> findRandomPublicUsers(@Param("myId") Long myId, @Param("limitCnt") int limitCnt);
}
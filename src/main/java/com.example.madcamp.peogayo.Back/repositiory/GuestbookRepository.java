package com.example.madcamp.peogayo.Back.repositiory;

import com.example.madcamp.peogayo.Back.entity.Guestbook;
import com.example.madcamp.peogayo.Back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {
    // 특정 owner의 방명록을 최신순으로 가져오기
    List<Guestbook> findAllByOwnerOrderByCreatedAtDesc(User owner);
}
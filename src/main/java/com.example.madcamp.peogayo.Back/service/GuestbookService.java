package com.example.madcamp.peogayo.Back.service;

import com.example.madcamp.peogayo.Back.dto.GuestbookRequest;
import com.example.madcamp.peogayo.Back.dto.GuestbookResponseDto;
import com.example.madcamp.peogayo.Back.entity.Guestbook;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.repository.GuestbookRepository;
import com.example.madcamp.peogayo.Back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestbookService {

    private final GuestbookRepository guestbookRepository;
    private final UserRepository userRepository;

    // 1. 방명록 작성
    @Transactional
    public void writeGuestbook(Long ownerId, User loginUser, GuestbookRequest request) {
        // 주인 찾기
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미니홈피입니다."));

        // 글쓴이 찾기
        User writer = userRepository.findById(loginUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("로그인 정보를 찾을 수 없습니다."));

        Guestbook guestbook = new Guestbook();
        guestbook.setOwner(owner);
        guestbook.setWriter(writer);
        guestbook.setContent(request.getContent());

        guestbookRepository.save(guestbook);
    }

    // 2. 방명록 목록 조회
    public List<GuestbookResponseDto> getGuestbookList(Long ownerId) {
        return guestbookRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(GuestbookResponseDto::from)
                .collect(Collectors.toList());
    }

    // 3. 방명록 삭제 (주인 or 글쓴이 둘 다 가능)
    @Transactional
    public void deleteGuestbook(Long guestbookId, User loginUser) {
        Guestbook guestbook = guestbookRepository.findById(guestbookId)
                .orElseThrow(() -> new IllegalArgumentException("방명록이 존재하지 않습니다."));

        Long loginId = loginUser.getId();
        Long writerId = guestbook.getWriter().getId();
        Long ownerId = guestbook.getOwner().getId();

        // 삭제 권한 체크: 내가 쓴 글이거나 OR 내 홈피에 달린 글이거나
        if (loginId.equals(writerId) || loginId.equals(ownerId)) {
            guestbookRepository.delete(guestbook);
        } else {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
    }
}
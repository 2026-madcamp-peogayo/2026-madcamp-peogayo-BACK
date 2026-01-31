package com.example.madcamp.peogayo.Back.service;

import com.example.madcamp.peogayo.Back.dto.FriendDto;
import com.example.madcamp.peogayo.Back.entity.Friend;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.repository.FriendRepository;
import com.example.madcamp.peogayo.Back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // 1. 친구 목록 조회 (공개 범위 확인 포함)
    // - 내가 추가했거나, 상대가 나를 추가한 모든 관계를 조회 (이미 로직이 양방향 조회로 되어 있음)
    public List<FriendDto> getFriendList(Long targetUserId, User loginUser) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // [비공개 체크]
        // 조회하는 사람이 주인이 아니라면 비공개 시 빈 리스트 반환
        boolean isOwner = (loginUser != null) && loginUser.getId().equals(targetUserId);

        if (targetUser.isFriendListPrivate() && !isOwner) {
            return new ArrayList<>();
        }

        // 친구 목록 DB에서 가져오기
        List<Friend> friends = friendRepository.findFriendsByUserId(targetUserId);

        List<FriendDto> result = new ArrayList<>();

        for (Friend f : friends) {
            User friendUser;
            // 관계에서 '나'를 제외한 '상대방'을 찾아서 DTO로 변환
            if (f.getRequester().getId().equals(targetUserId)) {
                friendUser = f.getReceiver();
            } else {
                friendUser = f.getRequester();
            }
            result.add(FriendDto.from(friendUser));
        }

        return result;
    }

    // 2. 일촌 맺기 / 팔로우 (수락 과정 없음, 즉시 연결)
    @Transactional
    public void addFriend(User requester, Long receiverId) {
        // 나 자신에게 신청 불가
        if (requester.getId().equals(receiverId)) {
            throw new IllegalArgumentException("자신을 친구로 추가할 수 없습니다.");
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 이미 친구인지 확인 (중복 방지)
        List<Friend> existing = friendRepository.findFriendship(requester.getId(), receiverId);
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("이미 친구 관계입니다.");
        }

        // 바로 ACCEPTED(친구) 상태로 생성
        Friend friend = new Friend();
        friend.setRequester(requester);
        friend.setReceiver(receiver);
        friend.setStatus(Friend.FriendStatus.ACCEPTED);

        friendRepository.save(friend);
    }

    // 3. 일촌 끊기 / 언팔로우
    @Transactional
    public void deleteFriend(User loginUser, Long friendId) {
        // 나와 상대방 사이의 관계를 찾음
        List<Friend> relationships = friendRepository.findFriendship(loginUser.getId(), friendId);

        if (relationships.isEmpty()) {
            throw new IllegalArgumentException("관계가 존재하지 않습니다.");
        }

        // 관계 삭제
        friendRepository.delete(relationships.get(0));
    }
}
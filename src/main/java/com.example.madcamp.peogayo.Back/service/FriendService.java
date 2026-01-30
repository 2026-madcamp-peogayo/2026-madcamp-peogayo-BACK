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
    public List<FriendDto> getFriendList(Long targetUserId, User loginUser) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // [비공개 체크]
        // 1. 친구 목록이 비공개 설정되어 있고
        // 2. 조회하는 사람이 주인이 아니라면 -> 빈 리스트 반환
        boolean isOwner = (loginUser != null) && loginUser.getId().equals(targetUserId);

        if (targetUser.isFriendListPrivate() && !isOwner) {
            return new ArrayList<>();
        }

        // 친구 목록 DB에서 가져오기
        List<Friend> friends = friendRepository.findFriendsByUserId(targetUserId);

        List<FriendDto> result = new ArrayList<>();

        for (Friend f : friends) {
            User friendUser;
            if (f.getRequester().getId().equals(targetUserId)) {
                friendUser = f.getReceiver();
            } else {
                friendUser = f.getRequester();
            }
            result.add(FriendDto.from(friendUser));
        }

        return result;
    }

    // 2. 일촌 신청 (친구 요청)
    @Transactional
    public void requestFriend(User requester, Long receiverId) {
        // 나 자신에게 신청 불가
        if (requester.getId().equals(receiverId)) {
            throw new IllegalArgumentException("자신에게 일촌 신청을 할 수 없습니다.");
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 이미 친구이거나, 이미 요청을 보낸 상태인지 확인 (중복 방지)
        List<Friend> existing = friendRepository.findFriendship(requester.getId(), receiverId);
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("이미 친구이거나 요청 대기 중인 상태입니다.");
        }

        // 일촌 신청 생성 (상태: WAITING)
        Friend friend = new Friend();
        friend.setRequester(requester);
        friend.setReceiver(receiver);
        friend.setStatus(Friend.FriendStatus.WAITING);

        friendRepository.save(friend);
    }

    // 3. 일촌 수락
    @Transactional
    public void acceptFriend(User loginUser, Long requesterId) {
        // 나와 상대방(requesterId) 사이의 관계를 찾음
        List<Friend> relationships = friendRepository.findFriendship(loginUser.getId(), requesterId);

        if (relationships.isEmpty()) {
            throw new IllegalArgumentException("친구 요청이 존재하지 않습니다.");
        }

        Friend friendRequest = relationships.get(0);

        // 이미 친구인지 확인
        if (friendRequest.getStatus() == Friend.FriendStatus.ACCEPTED) {
            throw new IllegalArgumentException("이미 일촌 관계입니다.");
        }

        // 권한 확인: 요청을 받은 사람만 수락 가능
        if (!friendRequest.getReceiver().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("수락 권한이 없습니다. (요청을 보낸 사람은 수락할 수 없음)");
        }

        // 상태 변경 WAITING -> ACCEPTED
        friendRequest.setStatus(Friend.FriendStatus.ACCEPTED);

    }

    // 4. 일촌 끊기 / 거절
    @Transactional
    public void deleteFriend(User loginUser, Long friendId) {
        List<Friend> relationships = friendRepository.findFriendship(loginUser.getId(), friendId);

        if (relationships.isEmpty()) {
            throw new IllegalArgumentException("관계가 존재하지 않습니다.");
        }

        // 관계 삭제
        friendRepository.delete(relationships.get(0));
    }
}
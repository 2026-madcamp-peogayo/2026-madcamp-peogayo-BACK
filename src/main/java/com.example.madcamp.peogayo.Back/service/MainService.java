package com.example.madcamp.peogayo.Back.service;

import com.example.madcamp.peogayo.Back.dto.PostResponseDto;
import com.example.madcamp.peogayo.Back.dto.RecommendUserDto;
import com.example.madcamp.peogayo.Back.entity.Friend;
import com.example.madcamp.peogayo.Back.entity.Post;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.repository.FriendRepository;
import com.example.madcamp.peogayo.Back.repository.PostRepository;
import com.example.madcamp.peogayo.Back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    // MAIN-01: 전체 글 보기 (공개 유저의 공개 글만)
    public List<PostResponseDto> getAllPublicPosts() {
        List<Post> posts = postRepository.findAllPublicPosts();
        return posts.stream()
                .map(PostResponseDto::from)
                .collect(Collectors.toList());
    }

    // MAIN-02: 친구 글 보기
    public List<PostResponseDto> getFriendPosts(User loginUser) {
        // 1. 내 친구 리스트 가져오기 (ACCEPTED 상태만)
        List<Friend> relationships = friendRepository.findFriendsByUserId(loginUser.getId());

        // 2. 친구들의 ID만 추출
        List<Long> friendIds = new ArrayList<>();
        for (Friend f : relationships) {
            if (f.getRequester().getId().equals(loginUser.getId())) {
                friendIds.add(f.getReceiver().getId());
            } else {
                friendIds.add(f.getRequester().getId());
            }
        }

        if (friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 친구 ID들로 게시글 조회
        List<Post> posts = postRepository.findPostsByFriendIds(friendIds);
        return posts.stream()
                .map(PostResponseDto::from)
                .collect(Collectors.toList());
    }

    // MAIN-04: 파도타기 (랜덤 유저 1명 ID 반환)
    public Long getRandomUserForSurfing(Long myId) {
        // 나를 제외한 공개 유저 1명 랜덤 조회
        List<User> randomUsers = userRepository.findRandomPublicUsers(myId, 1);

        if (randomUsers.isEmpty()) {
            throw new IllegalArgumentException("파도타기 할 공개 유저가 없습니다");
        }
        return randomUsers.get(0).getId();
    }

    // MAIN-05: 오늘의 추천 (랜덤 유저 3명 리스트 반환)
    public List<RecommendUserDto> getTodayRecommendations(Long myId) {
        // 나를 제외한 공개 유저 3명 랜덤 조회
        List<User> users = userRepository.findRandomPublicUsers(myId, 3);

        return users.stream()
                .map(RecommendUserDto::from)
                .collect(Collectors.toList());
    }
}
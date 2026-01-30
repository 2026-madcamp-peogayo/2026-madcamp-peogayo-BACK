package com.example.madcamp.peogayo.Back.service;

import com.example.madcamp.peogayo.Back.dto.HomeResponseDto;
import com.example.madcamp.peogayo.Back.dto.HomeUpdateRequest;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final UserRepository userRepository;
    private final FileService fileService;

    // 마이홈 정보 조회
    public HomeResponseDto getHomeInfo(Long targetUserId, User loginUser) {
        // 1. 보고 싶은 홈피 주인 찾기 - targetUserId
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 내 홈피인지 확인 - loginUser
        // 로그인 안 했으면 false, 로그인 했고 ID 같으면 true
        boolean isMyHome = (loginUser != null) && targetUser.getId().equals(loginUser.getId());

        // 3. DTO로 변환해서 리턴
        return HomeResponseDto.from(targetUser, isMyHome);
    }

    // 2. 마이홈 설정 수정
    @Transactional
    public User updateHomeProfile(User loginUser, HomeUpdateRequest request,
                                  MultipartFile profileImage, MultipartFile bgImage) throws IOException { // 인자 추가됨
        // DB에서 다시 조회
        User user = userRepository.findById(loginUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("로그인 정보를 찾을 수 없습니다."));

        // 값이 있는 경우에만 변경
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getGreeting() != null) user.setGreeting(request.getGreeting());
        if (request.getProfileImageUrl() != null) user.setProfileImageUrl(request.getProfileImageUrl());
        if (request.getBgImageUrl() != null) user.setBgImageUrl(request.getBgImageUrl());

        // 설정값 변경
        if (request.getIsHomePrivate() != null) user.setHomePrivate(request.getIsHomePrivate());
        if (request.getIsFriendListPrivate() != null) user.setFriendListPrivate(request.getIsFriendListPrivate());

        // [추가됨] 이미지 파일이 들어왔을 경우 업로드 후 URL 덮어쓰기
        if (profileImage != null && !profileImage.isEmpty()) {
            String profileUrl = fileService.uploadFile(profileImage);
            user.setProfileImageUrl(profileUrl);
        }

        if (bgImage != null && !bgImage.isEmpty()) {
            String bgUrl = fileService.uploadFile(bgImage);
            user.setBgImageUrl(bgUrl);
        }

        return user;
    }
}
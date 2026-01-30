package com.example.madcamp.peogayo.Back.service;

import com.example.madcamp.peogayo.Back.dto.PostCreateDto;
import com.example.madcamp.peogayo.Back.dto.PostResponseDto;
import com.example.madcamp.peogayo.Back.entity.Friend;
import com.example.madcamp.peogayo.Back.entity.Post;
import com.example.madcamp.peogayo.Back.entity.User;
import com.example.madcamp.peogayo.Back.repository.FriendRepository;
import com.example.madcamp.peogayo.Back.repository.PostRepository;
import com.example.madcamp.peogayo.Back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FileService fileService;

    // 1. 게시글 작성
    @Transactional
    public PostResponseDto createPost(User user, PostCreateDto requestDto, MultipartFile imageFile) throws IOException {

        // 그림판 앱 특성상 이미지가 없으면 의미가 없음
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("등록할 게시물이 없습니다.");
        }

        // 1. 이미지 파일 저장
        String imageUrl = fileService.uploadFile(imageFile);

        // 2. 엔티티 생성
        Post post = new Post();
        post.setWriter(user);
        post.setContentText(requestDto.getContentText());

        // 저장된 이미지 경로 DB에 넣기
        post.setContentImageUrl(imageUrl);

        // 공개범위 설정
        if (requestDto.getVisibility() == null) {
            post.setVisibility(Post.Visibility.PUBLIC);
        } else {
            post.setVisibility(requestDto.getVisibility());
        }

        // 3. DB 저장
        postRepository.save(post);

        // 4. 결과 반환
        return PostResponseDto.from(post);
    }

    // 2. 게시글 목록 조회
    public List<PostResponseDto> getPostList(Long targetUserId, User loginUser) {
        // 1. targetUserId가 쓴 글들을 싹 가져옴
        List<Post> posts = postRepository.findAllByWriterIdOrderByCreatedAtDesc(targetUserId);

        // 2. 권한 체크를 위한 준비
        boolean isOwner = (loginUser != null) && loginUser.getId().equals(targetUserId);
        boolean isFriend = false;

        // 로그인했고 주인이 아니라면 -> 친구 관계인지 DB 조회
        if (loginUser != null && !isOwner) {
            List<Friend> friendship = friendRepository.findFriendship(loginUser.getId(), targetUserId);

            // 데이터가 존재하고, 상태가 ACCEPTED여야 친구
            if (!friendship.isEmpty() && friendship.get(0).getStatus() == Friend.FriendStatus.ACCEPTED) {
                isFriend = true;
            }
        }

        // 3. 필터링
        boolean finalIsFriend = isFriend;

        return posts.stream()
                .filter(post -> {
                    // 주인이면 -> 다 보여줌
                    if (isOwner) return true;

                    // 전체 공개면 -> 누구나 보여줌
                    if (post.getVisibility() == Post.Visibility.PUBLIC) return true;

                    // 친구 공개면 -> 친구한테만 보여줌
                    if (post.getVisibility() == Post.Visibility.FRIENDS && finalIsFriend) return true;

                    // 비공개거나 친구 아닌데 친구공개 글인 경우 -> 숨김
                    return false;
                })
                .map(PostResponseDto::from)
                .collect(Collectors.toList());
    }

    // 3. 게시글 삭제
    @Transactional
    public void deletePost(Long postId, User loginUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 삭제하려는 사람이 글쓴이인지 확인
        if (!post.getWriter().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }
}
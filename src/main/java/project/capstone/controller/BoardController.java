package project.capstone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.capstone.common.ApiResponseDto;
import project.capstone.common.SuccessResponse;
import project.capstone.dto.BoardRequestsDto;
import project.capstone.dto.BoardResponseDto;
import project.capstone.entity.User;
import project.capstone.service.BoardService;
import project.capstone.service.LikeService;
import project.capstone.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;
    private final LikeService likeService;

    // 게시글 작성
    @PostMapping("/api/post")
    public ApiResponseDto<BoardResponseDto> createPost(@RequestBody BoardRequestsDto requestsDto) {
        return boardService.createPost(requestsDto);
    }

    // 게시글 전체 목록 조회
    @GetMapping("/api/posts")
    public ApiResponseDto<List<BoardResponseDto>> getPosts() {
        return boardService.getPosts();
    }

    // 선택된 게시글 조회
    @GetMapping("/api/post/{id}")
    public ApiResponseDto<BoardResponseDto> getPost(@PathVariable("id") Long id) {
        User user = getCurrentUser();  // 현재 사용자를 가져옴
        return boardService.getPost(id, user);  // 가져온 사용자 정보를 서비스로 전달
    }

    // 선택된 게시글 수정
    @PutMapping("/api/post/{id}")
    public ApiResponseDto<BoardResponseDto> updatePost(@PathVariable("id") Long id,
                                                       @RequestBody BoardRequestsDto requestsDto) {
        User user = getCurrentUser();
        return boardService.updatePost(id, requestsDto, user);
    }

    // 선택된 게시글 삭제
    @DeleteMapping("/api/post/{id}")
    public ApiResponseDto<SuccessResponse> deletePost(@PathVariable("id") Long id) {
        User user = getCurrentUser();
        return boardService.deletePost(id, user);
    }

    // 게시글 좋아요 추가 및 취소
    @PostMapping("/api/post/{id}/likes")
    public ApiResponseDto<String> likePost(@PathVariable("id") Long id) {
        User user = getCurrentUser();  // 현재 사용자를 가져옴
        boolean liked = likeService.likePost(id, user);  // 좋아요 추가 또는 취소

        if (!liked) {
            return ApiResponseDto.success("좋아요 취소");
        }
        return ApiResponseDto.success("좋아요를 눌렀습니다.");
    }

    // 현재 인증된 사용자 정보를 가져오는 메서드
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            return userService.findByEmail(userDetails.getUsername());
        } else {
            // 인증되지 않은 경우에 대한 처리
            throw new IllegalStateException("User is not authenticated");
        }
    }

}

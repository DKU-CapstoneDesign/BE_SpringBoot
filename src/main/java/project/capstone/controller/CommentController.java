package project.capstone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.capstone.common.ApiResponseDto;
import project.capstone.common.SuccessResponse;
import project.capstone.dto.CommentRequestDto;
import project.capstone.dto.CommentResponseDto;
import project.capstone.entity.User;
import project.capstone.service.CommentService;
import project.capstone.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    // 댓글 작성
    @PostMapping("/comment/{id}")
    public ApiResponseDto<CommentResponseDto> createComment(@PathVariable("id") Long id, @RequestBody CommentRequestDto requestDto) {
        User user = getCurrentUser();

        System.out.println("Contents: " + requestDto.getContents());
        System.out.println("ParentCommentId: " + requestDto.getParentCommentId());

        return commentService.createComment(id, requestDto, user);
    }

    // 댓글 수정
    @PutMapping("/comment/{id}")
    public ApiResponseDto<CommentResponseDto> updateComment(@PathVariable("id") Long id, @RequestBody CommentRequestDto requestDto) {
        User user = getCurrentUser();
        return commentService.updateComment(id, requestDto, user);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{id}")
    public ApiResponseDto<SuccessResponse> deleteComment(@PathVariable("id") Long id) {
        User user = getCurrentUser();
        return commentService.deleteComment(id, user);
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

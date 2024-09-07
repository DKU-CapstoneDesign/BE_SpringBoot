package project.capstone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.capstone.common.ApiResponseDto;
import project.capstone.common.SuccessResponse;
import project.capstone.dto.CommentRequestDto;
import project.capstone.dto.CommentResponseDto;
import project.capstone.security.UserDetailsImpl;
import project.capstone.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/comment/{id}")
    public ApiResponseDto<CommentResponseDto> createComment(@PathVariable("id") Long id, @RequestBody CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.createComment(id, requestDto, userDetails.getUser());
    }

    // 댓글 수정
    @PutMapping("/comment/{id}")
    public ApiResponseDto<CommentResponseDto> updateComment(@PathVariable("id") Long id, @RequestBody CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.updateComment(id, requestDto, userDetails.getUser());
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{id}")
    public ApiResponseDto<SuccessResponse> deleteComment(@PathVariable("id") Long id,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.deleteComment(id, userDetails.getUser());
    }

}
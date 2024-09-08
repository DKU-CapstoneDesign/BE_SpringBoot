package project.capstone.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import project.capstone.common.ApiResponseDto;
import project.capstone.common.ResponseUtils;
import project.capstone.common.SuccessResponse;
import project.capstone.dto.CommentRequestDto;
import project.capstone.dto.CommentResponseDto;
import project.capstone.entity.Board;
import project.capstone.entity.Comment;
import project.capstone.entity.User;
import project.capstone.entity.enumSet.ErrorType;
import project.capstone.entity.enumSet.UserRoleEnum;
import project.capstone.exception.RestApiException;
import project.capstone.repository.BoardRepository;
import project.capstone.repository.CommentRepository;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    // 댓글 작성
    @Transactional
    public ApiResponseDto<CommentResponseDto> createComment(Long boardId, CommentRequestDto requestDto, User user) {
        // 선택한 게시글 DB 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RestApiException(ErrorType.NOT_FOUND_WRITING));

        Long parentCommentId = requestDto.getParentCommentId();
        Comment comment = Comment.of(requestDto, board, user);

        if (parentCommentId != null) {
            // 부모 댓글이 있는 경우
            Comment parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RestApiException(ErrorType.NOT_FOUND_WRITING));
            parentComment.addChildComment(comment); // 부모 댓글에 자식 댓글 추가
            commentRepository.save(parentComment); // 부모 댓글 저장
        }

        // 자식 댓글 저장
        commentRepository.save(comment);

        return ResponseUtils.ok(CommentResponseDto.from(comment));
    }

    // 댓글 수정
    @Transactional
    public ApiResponseDto<CommentResponseDto> updateComment(Long id, CommentRequestDto requestDto, User user) {
        // 선택한 댓글이 DB에 있는지 확인
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ErrorType.NOT_FOUND_WRITING));

        // 댓글의 작성자와 수정하려는 사용자의 정보가 일치하는지 확인
        if (!comment.getUser().equals(user) && user.getRole() == UserRoleEnum.USER) {
            throw new RestApiException(ErrorType.NOT_WRITER);
        }

        // 댓글 수정
        comment.update(requestDto, user);
        commentRepository.save(comment); // 수정된 댓글 저장

        return ResponseUtils.ok(CommentResponseDto.from(comment));
    }

    // 댓글 삭제
    @Transactional
    public ApiResponseDto<SuccessResponse> deleteComment(Long id, User user) {
        // 선택한 댓글이 DB에 있는지 확인
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ErrorType.NOT_FOUND_WRITING));

        // 댓글의 작성자와 삭제하려는 사용자의 정보가 일치하는지 확인
        if (!comment.getUser().equals(user) && user.getRole() == UserRoleEnum.USER) {
            throw new RestApiException(ErrorType.NOT_WRITER);
        }

        // 댓글 삭제
        commentRepository.deleteById(id);

        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "댓글 삭제 성공"));
    }
}

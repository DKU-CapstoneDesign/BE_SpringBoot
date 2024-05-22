package project.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.capstone.common.ApiResponseDto;
import project.capstone.common.ResponseUtils;
import project.capstone.dto.BoardResponseDto;
import project.capstone.dto.CommentResponseDto;
import project.capstone.entity.Board;
import project.capstone.entity.Comment;
import project.capstone.entity.Likes;
import project.capstone.entity.User;
import project.capstone.entity.enumSet.ErrorType;
import project.capstone.exception.RestApiException;
import project.capstone.repository.BoardRepository;
import project.capstone.repository.CommentRepository;
import project.capstone.repository.LikesRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    // 게시글 좋아요 기능
    public ApiResponseDto<BoardResponseDto> likePost(Long id, User user) {
        // 선택한 게시글이 DB에 있는지 확인
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty()) {
            throw new RestApiException(ErrorType.NOT_FOUND_WRITING);
        }

        // 이전에 좋아요 누른 적 있는지 확인
        Optional<Likes> found = likesRepository.findByBoardAndUser(board.get(), user);
        if (found.isEmpty()) {  // 좋아요 누른적 없음
            Likes likes = Likes.of(board.get(), user);
            likesRepository.save(likes);
        } else { // 좋아요 누른 적 있음
            likesRepository.delete(found.get()); // 좋아요 눌렀던 정보를 지운다.
            likesRepository.flush();
        }

        return ResponseUtils.ok(BoardResponseDto.from(board.get()));
    }

    // 댓글 좋아요 기능
    public ApiResponseDto<CommentResponseDto> likeComment(Long id, User user) {
        // 선택한 댓글이 DB에 있는지 확인
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            throw new RestApiException(ErrorType.NOT_FOUND_WRITING);
        }

        // 이전에 좋아요 누른 적 있는지 확인
        Optional<Likes> found = likesRepository.findByCommentAndUser(comment.get(), user);
        if (found.isEmpty()) {  // 좋아요 누른적 없음
            Likes likes = Likes.of(comment.get(), user);
            likesRepository.save(likes);
        } else { // 좋아요 누른 적 있음
            likesRepository.delete(found.get()); // 좋아요 눌렀던 정보를 지운다.
            likesRepository.flush();
        }

        return ResponseUtils.ok(CommentResponseDto.from(comment.get()));
    }
}
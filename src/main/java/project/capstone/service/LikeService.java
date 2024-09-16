package project.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.capstone.common.ApiResponseDto;
import project.capstone.dto.BoardResponseDto;
import project.capstone.entity.Board;
import project.capstone.entity.Likes;
import project.capstone.entity.User;
import project.capstone.entity.enumSet.ErrorType;
import project.capstone.exception.RestApiException;
import project.capstone.repository.BoardRepository;
import project.capstone.repository.LikesRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;

    // 게시글 좋아요 기능
    public boolean likePost(Long id, User user) {
        // 선택한 게시글이 DB에 있는지 확인
        Optional<Board> boardOpt = boardRepository.findById(id);
        if (boardOpt.isEmpty()) {
            throw new RestApiException(ErrorType.NOT_FOUND_WRITING);
        }

        Board board = boardOpt.get();
        // 이전에 좋아요 누른 적 있는지 확인
        Optional<Likes> found = likesRepository.findByBoardAndUser(board, user);
        if (found.isEmpty()) {  // 좋아요 누른적 없음
            Likes likes = Likes.of(board, user);
            likesRepository.save(likes);
            board.incrementLikes();
            boardRepository.save(board);
            return true;
        } else { // 좋아요 누른 적 있음
            likesRepository.delete(found.get());
            likesRepository.flush();
            board.decrementLikes();
            boardRepository.save(board);
            return false;
        }
    }
}

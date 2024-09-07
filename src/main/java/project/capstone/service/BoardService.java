package project.capstone.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.capstone.common.ApiResponseDto;
import project.capstone.common.ResponseUtils;
import project.capstone.common.SuccessResponse;
import project.capstone.dto.BoardRequestsDto;
import project.capstone.dto.BoardResponseDto;
import project.capstone.dto.CommentResponseDto;
import project.capstone.entity.Board;
import project.capstone.entity.Comment;
import project.capstone.entity.User;
import project.capstone.entity.enumSet.ErrorType;
import project.capstone.entity.enumSet.UserRoleEnum;
import project.capstone.exception.RestApiException;
import project.capstone.repository.BoardRepository;
import project.capstone.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 게시글 전체 목록 조회
    @Transactional(readOnly = true) // 수정 없이 읽기만
    public ApiResponseDto<List<BoardResponseDto>> getPosts() {

        List<Board> boardList = boardRepository.findAllByOrderByModifiedAtDesc();
        List<BoardResponseDto> responseDtoList = new ArrayList<>();

        for (Board board : boardList) {
            // 댓글리스트 작성일자 기준 내림차순 정렬
            board.getCommentList()
                    .sort(Comparator.comparing(Comment::getModifiedAt)
                            .reversed());

            // 대댓글은 제외 부분 작성
            List<CommentResponseDto> commentList = new ArrayList<>();
            for (Comment comment : board.getCommentList()) {
                if (comment.getParentCommentId() == null) {
                    commentList.add(CommentResponseDto.from(comment));
                }
            }

            // List<BoardResponseDto> 로 만들기 위해 board 를 BoardResponseDto 로 만들고, list 에 dto 를 하나씩 넣는다.
            responseDtoList.add(BoardResponseDto.from(board, commentList));
        }

        return ResponseUtils.ok(responseDtoList);

    }

    // 게시글 작성
    @Transactional
    public ApiResponseDto<BoardResponseDto> createPost(BoardRequestsDto requestsDto) {
        log.info("게시글 작성 요청 수신: {}", requestsDto);

        // 사용자 조회
        User user = userRepository.findById(requestsDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + requestsDto.getUserId()));

        // 게시글 생성
        Board board = new Board();
        board.setUser(user);
        board.setTitle(requestsDto.getTitle());
        board.setContents(requestsDto.getContents());

        // 게시글 저장
        Board returnBoard = boardRepository.save(board);
        log.info("게시글이 저장되었습니다. board: {}", returnBoard);

        // BoardResponseDto로 변환 후 응답 반환
        return ResponseUtils.ok(BoardResponseDto.from(returnBoard));
    }


    // 선택된 게시글 조회
    @Transactional(readOnly = true)
    public ApiResponseDto<BoardResponseDto> getPost(Long id) {
        // Id에 해당하는 게시글이 있는지 확인
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty()) { // 해당 게시글이 없다면
            throw new RestApiException(ErrorType.NOT_FOUND_WRITING);
        }

        // 댓글리스트 작성일자 기준 내림차순 정렬
        board.get()
                .getCommentList()
                .sort(Comparator.comparing(Comment::getModifiedAt)
                        .reversed());

        // 대댓글은 제외 부분 작성
        List<CommentResponseDto> commentList = new ArrayList<>();
        for (Comment comment : board.get().getCommentList()) {
            if (comment.getParentCommentId() == null) {
                commentList.add(CommentResponseDto.from(comment));
            }
        }

        // board 를 responseDto 로 변환 후, ResponseEntity body 에 dto 담아 리턴
        return ResponseUtils.ok(BoardResponseDto.from(board.get(), commentList));
    }

    // 선택된 게시글 수정
    @Transactional
    public ApiResponseDto<BoardResponseDto> updatePost(Long id, BoardRequestsDto requestsDto, User user) {

        // 선택한 게시글이 DB에 있는지 확인
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty()) {
            throw new RestApiException(ErrorType.NOT_FOUND_WRITING);
        }

        // 선택한 게시글의 작성자와 토큰에서 가져온 사용자 정보가 일치하는지 확인 (수정하려는 사용자가 관리자라면 게시글 수정 가능)
        Optional<Board> found = boardRepository.findByIdAndUser(id, user);
        if (found.isEmpty() && user.getRole() == UserRoleEnum.USER) { // 일치하는 게시물이 없다면
            throw new RestApiException(ErrorType.NOT_WRITER);
        }

        // 게시글 id 와 사용자 정보 일치한다면, 게시글 수정
        board.get().update(requestsDto, user);
        boardRepository.flush(); // responseDto 에 modifiedAt 업데이트 해주기 위해 flush 사용

        return ResponseUtils.ok(BoardResponseDto.from(board.get()));

    }

    // 게시글 삭제
    @Transactional
    public ApiResponseDto<SuccessResponse> deletePost(Long id, User user) {

        // 선택한 게시글이 DB에 있는지 확인
        Optional<Board> found = boardRepository.findById(id);
        if (found.isEmpty()) {
            throw new RestApiException(ErrorType.NOT_FOUND_WRITING);
        }

        // 선택한 게시글의 작성자와 토큰에서 가져온 사용자 정보가 일치하는지 확인 (삭제하려는 사용자가 관리자라면 게시글 삭제 가능)
        Optional<Board> board = boardRepository.findByIdAndUser(id, user);
        if (board.isEmpty() && user.getRole() == UserRoleEnum.USER) { // 일치하는 게시물이 없다면
            throw new RestApiException(ErrorType.NOT_WRITER);
        }

        // 게시글 id 와 사용자 정보 일치한다면, 게시글 수정
        boardRepository.deleteById(id);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "게시글 삭제 성공"));

    }

}
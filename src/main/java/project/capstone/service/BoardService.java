package project.capstone.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.capstone.common.ApiResponseDto;
import project.capstone.common.ResponseUtils;
import project.capstone.common.SuccessResponse;
import project.capstone.dto.BoardRequestsDto;
import project.capstone.dto.BoardResponseDto;
import project.capstone.dto.CommentResponseDto;
import project.capstone.entity.*;
import project.capstone.entity.enumSet.ErrorType;
import project.capstone.entity.enumSet.UserRoleEnum;
import project.capstone.exception.RestApiException;
import project.capstone.repository.AttachmentRepository;
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
    private final AttachmentRepository attachmentRepository;
    private final MinioService minioService;

    // 게시글 전체 목록 조회
    @Transactional(readOnly = true)
    public ApiResponseDto<List<BoardResponseDto>> getPosts() {
        List<Board> boardList = boardRepository.findAllByOrderByCreatedAtDesc();
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
    public ApiResponseDto<BoardResponseDto> createPost(
            Long userId,
            String title,
            String contents,
            Board.Category category,
            List<MultipartFile> attachments // 파일은 선택사항
    ) {
        log.info("게시글 작성 요청 수신: userId={}, title={}, contents={}, category={}", userId, title, contents, category);

        // 사용자 확인
        if (userId == null) {
            throw new IllegalArgumentException("User ID는 null일 수 없습니다.");
        }

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 게시글 생성
        Board board = new Board();
        board.setUser(user);
        board.setTitle(title);
        board.setContents(contents);
        board.setCategory(category);

        // 게시글 저장
        Board savedBoard = boardRepository.save(board);
        log.info("게시글이 저장되었습니다. board: {}", savedBoard);

        // 파일 처리
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    try {
                        // MinIO에 파일 업로드
                        String fileName = file.getOriginalFilename();
                        String filePath = minioService.uploadFile(file);

                        // 업로드된 파일 정보를 Attachment 엔티티로 저장
                        Attachment attachment = new Attachment(savedBoard, fileName, filePath);
                        attachmentRepository.save(attachment);
                    } catch (Exception e) {
                        log.error("파일 업로드 실패", e);
                        throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
                    }
                }
            }
        }

        // BoardResponseDto로 변환 후 응답 반환
        return ResponseUtils.ok(BoardResponseDto.from(savedBoard));
    }

    // 선택된 게시글 조회
    @Transactional
    public ApiResponseDto<BoardResponseDto> getPost(Long id, User currentUser) {
        if (id == null) {
            throw new IllegalArgumentException("ID 값이 null일 수 없습니다.");
        }
        // Id에 해당하는 게시글이 있는지 확인
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isEmpty()) { // 해당 게시글이 없다면
            throw new RestApiException(ErrorType.NOT_FOUND_WRITING);
        }

        Board board = boardOptional.get();

        // 현재 사용자가 게시글 작성자가 아닐 경우 조회수 증가
        if (!board.getUser().getId().equals(currentUser.getId())) {
            board.setViewCount(board.getViewCount() + 1);
            boardRepository.flush(); // 즉시 DB에 반영
        }

        // 댓글리스트 작성일자 기준 내림차순 정렬
        board.getCommentList().sort(Comparator.comparing(Comment::getModifiedAt).reversed());

        // 대댓글은 제외 부분 작성
        List<CommentResponseDto> commentList = new ArrayList<>();
        for (Comment comment : board.getCommentList()) {
            if (comment.getParentCommentId() == null) {
                commentList.add(CommentResponseDto.from(comment));
            }
        }

        // board 를 responseDto 로 변환 후, ResponseEntity body 에 dto 담아 리턴
        return ResponseUtils.ok(BoardResponseDto.from(board, commentList));
    }

    // 선택된 게시글 수정
    @Transactional
    public ApiResponseDto<BoardResponseDto> updatePost(Long id, BoardRequestsDto requestsDto, User user) {
        // 선택한 게시글이 DB에 있는지 확인
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isEmpty()) {
            throw new RestApiException(ErrorType.NOT_FOUND_WRITING);
        }

        Board board = boardOptional.get();

        // 선택한 게시글의 작성자와 토큰에서 가져온 사용자 정보가 일치하는지 확인
        Optional<Board> found = boardRepository.findByIdAndUser(id, user);
        if (found.isEmpty() && user.getRole() == UserRoleEnum.USER) {
            throw new RestApiException(ErrorType.NOT_WRITER);
        }

        // 기존 파일 삭제 (필요에 따라 MinIO에서 파일 삭제 로직 추가 가능)
        List<Attachment> attachments = attachmentRepository.findByBoard(board);
        for (Attachment attachment : attachments) {
            try {
                // MinIO에서 파일 삭제
                minioService.removeFile(attachment.getFileName());
                attachmentRepository.delete(attachment); // 데이터베이스에서 파일 삭제
            } catch (Exception e) {
                log.error("파일 삭제 실패", e);
                // 파일 삭제 실패 시 예외 처리 로직 추가 가능
            }
        }

        // 파일 처리
        if (requestsDto.getAttachments() != null && !requestsDto.getAttachments().isEmpty()) {
            for (MultipartFile file : requestsDto.getAttachments()) {
                if (!file.isEmpty()) {
                    try {
                        // MinIO에 파일 업로드
                        String fileName = file.getOriginalFilename();
                        String filePath = minioService.uploadFile(file);

                        // 업로드된 파일 정보를 Attachment 엔티티로 저장
                        Attachment attachment = new Attachment(board, fileName, filePath);
                        attachmentRepository.save(attachment);
                    } catch (Exception e) {
                        log.error("파일 업로드 실패", e);
                        throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
                    }
                }
            }
        }

        // 게시글 수정
        board.update(requestsDto, user);
        boardRepository.flush(); // 즉시 DB에 반영

        return ResponseUtils.ok(BoardResponseDto.from(board));
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

        // 게시글과 연관된 파일 삭제
        List<Attachment> attachments = attachmentRepository.findByBoard(board.get());
        for (Attachment attachment : attachments) {
            try {
                // MinIO에서 파일 삭제
                minioService.removeFile(attachment.getFileName());
                attachmentRepository.delete(attachment); // 데이터베이스에서 파일 삭제
            } catch (Exception e) {
                log.error("파일 삭제 실패", e);
                // 파일 삭제 실패 시 예외 처리 로직 추가 가능
            }
        }

        // 게시글 삭제
        boardRepository.deleteById(id);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "게시글 삭제 성공"));
    }
}

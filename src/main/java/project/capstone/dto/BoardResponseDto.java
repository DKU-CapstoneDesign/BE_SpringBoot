package project.capstone.dto;

import lombok.Getter;
import project.capstone.entity.Board;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BoardResponseDto {
    private final Long id;
    private final String title;
    private final String contents;
    private final String username;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final Integer likeCount;
    private final List<CommentResponseDto> commentList;

    private BoardResponseDto(Board entity, List<CommentResponseDto> list) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.contents = entity.getContents();
        this.username = entity.getUser().getUsername();
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();
        this.likeCount = entity.getLikesList() != null ? entity.getLikesList().size() : 0;
        this.commentList = list;
    }

    private BoardResponseDto(Board entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.contents = entity.getContents();
        this.username = entity.getUser().getUsername();
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();
        this.likeCount = entity.getLikesList() != null ? entity.getLikesList().size() : 0;
        this.commentList = entity.getCommentList().stream().map(CommentResponseDto::from).collect(Collectors.toList());
    }

    public static BoardResponseDto from(Board entity, List<CommentResponseDto> list) {
        return new BoardResponseDto(entity, list);
    }

    public static BoardResponseDto from(Board entity) {
        return new BoardResponseDto(entity);
    }
}
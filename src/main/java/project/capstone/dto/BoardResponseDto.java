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
    private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final Integer likeCount;
    private final Board.Category category;
    private final List<CommentResponseDto> commentList;
    private final List<AttachmentResponseDto> attachments;

    private BoardResponseDto(Board entity, List<CommentResponseDto> list) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.contents = entity.getContents();
        this.nickname = entity.getUser().getNickname();
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();
        this.likeCount = entity.getLikesList() != null ? entity.getLikesList().size() : 0;
        this.category = entity.getCategory();
        this.commentList = list;
        this.attachments = entity.getAttachments().stream()
                .map(AttachmentResponseDto::from)
                .collect(Collectors.toList());
    }

    private BoardResponseDto(Board entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.contents = entity.getContents();
        this.nickname = entity.getUser().getNickname();
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();
        this.likeCount = entity.getLikesList() != null ? entity.getLikesList().size() : 0;
        this.category = entity.getCategory();
        this.commentList = entity.getCommentList().stream().map(CommentResponseDto::from).collect(Collectors.toList());
        this.attachments = entity.getAttachments().stream()
                .map(AttachmentResponseDto::from)
                .collect(Collectors.toList());
    }

    public static BoardResponseDto from(Board entity, List<CommentResponseDto> list) {
        return new BoardResponseDto(entity, list);
    }

    public static BoardResponseDto from(Board entity) {
        return new BoardResponseDto(entity);
    }
}

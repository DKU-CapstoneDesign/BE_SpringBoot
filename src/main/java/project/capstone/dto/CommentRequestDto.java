package project.capstone.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    private String contents;
    private Long parentCommentId;
}
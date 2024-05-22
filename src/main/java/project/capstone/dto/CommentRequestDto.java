package project.capstone.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    String contents;
    Long parentCommentId;
}
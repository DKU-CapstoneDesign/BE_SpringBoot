package project.capstone.dto;

import lombok.Getter;
import lombok.Setter;
import project.capstone.entity.Board;

@Getter
@Setter
public class BoardRequestsDto {
    private String title;
    private String contents;
    private Long userId;
    private Board.Category category;
}
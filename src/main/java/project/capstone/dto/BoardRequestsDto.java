package project.capstone.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequestsDto {
    private String title;
    private String contents;
    private Long userId;
}
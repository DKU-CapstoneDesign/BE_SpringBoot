package project.capstone.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import project.capstone.entity.Board;

import java.util.List;

@Getter
@Setter
public class BoardRequestsDto {
    private Long userId;
    private String title;
    private String contents;
    private Board.Category category;
    private List<MultipartFile> attachments;

    public BoardRequestsDto(Long userId, String title, String contents, Board.Category category, List<MultipartFile> attachments) {
        this.userId = userId;
        this.title = title;
        this.contents = contents;
        this.category = category;
        this.attachments = attachments;
    }
}
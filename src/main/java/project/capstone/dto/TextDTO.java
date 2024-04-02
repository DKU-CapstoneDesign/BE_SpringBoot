package project.capstone.dto;

import lombok.*;
import project.capstone.entity.TextEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TextDTO {
    private Long id;
    private String textWriter;
    private String textTitle;
    private String textContents;

    private int textHits;
    private LocalDateTime textCreatedTime;
}

package project.capstone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import project.capstone.dto.TextDTO;

@Entity
@Getter
@Setter
@Table(name = "text_table")
public class TextEntity extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String textWriter;

    @Column
    private String textTitle;

    @Column(length = 1000)
    private String textContents;

    @Column
    private int textHits;

    public static TextEntity toTextEntity(TextDTO textDTO) {
        TextEntity textEntity = new TextEntity();
        textEntity.setTextWriter(textDTO.getTextWriter());
        textEntity.setTextTitle(textDTO.getTextTitle());
        textEntity.setTextContents(textDTO.getTextContents());
        textEntity.setTextHits(0);
        return textEntity;
    }

}

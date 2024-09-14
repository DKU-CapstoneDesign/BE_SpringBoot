package project.capstone.entity;


import jakarta.persistence.*;
import lombok.*;
import project.capstone.dto.BoardRequestsDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Board extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 65535)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Likes> likesList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Board.Category category;  // 카테고리 필드 추가

    @Column(name = "view_count", nullable = false, columnDefinition = "int default 0")
    private int viewCount;

    // 카테고리 Enum 정의
    public enum Category {
        TRAVELING, HELPING, ANY, HOT
    }

    @Builder
    private Board(BoardRequestsDto requestsDto, User user) {
        this.title = requestsDto.getTitle();
        this.contents = requestsDto.getContents();
        this.user = user;
        this.category = requestsDto.getCategory();  // 카테고리 설정
    }

    public void update(BoardRequestsDto requestsDto, User user) {
        this.title = requestsDto.getTitle();
        this.contents = requestsDto.getContents();
        this.user = user;
        this.category = requestsDto.getCategory();  // 카테고리 업데이트
    }

    public static Board of(BoardRequestsDto requestsDto, User user) {
        return Board.builder()
                .requestsDto(requestsDto)
                .user(user)
                .build();
    }
}
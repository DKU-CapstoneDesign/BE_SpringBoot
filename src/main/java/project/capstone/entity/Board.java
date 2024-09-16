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
    private Board.Category category;

    @Column(name = "view_count", nullable = false, columnDefinition = "int default 0")
    private int viewCount;

    @Column(name = "likes_count", nullable = false, columnDefinition = "int default 0")
    private int likesCount;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Attachment> attachments = new ArrayList<>();

    // 카테고리 Enum 정의
    public enum Category {
        TRAVELING, HELPING, ANY, HOT
    }

    @Builder
    private Board(BoardRequestsDto requestsDto, User user) {
        this.title = requestsDto.getTitle();
        this.contents = requestsDto.getContents();
        this.user = user;
        this.category = requestsDto.getCategory();
    }

    public void update(BoardRequestsDto requestsDto, User user) {
        this.title = requestsDto.getTitle();
        this.contents = requestsDto.getContents();
        this.user = user;
        this.category = requestsDto.getCategory();
    }

    public static Board of(BoardRequestsDto requestsDto, User user) {
        return Board.builder()
                .requestsDto(requestsDto)
                .user(user)
                .build();
    }

    public void incrementLikes() {
        this.likesCount++;
    }

    public void decrementLikes() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }
}
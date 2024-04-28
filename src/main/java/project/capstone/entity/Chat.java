package project.capstone.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "chat")
public class Chat {
    @Id
    private String id; // 채팅 식별 번호
    private String msg;
    private String sender; // 송신자
    private String receiver; // 수신자
    private Long roomNum; // 방 번호
    private boolean read; // 읽기 여부
    private LocalDateTime createdAt; // 전송 시각
}

package project.capstone.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "chat_message")
public class ChatMessage {

    @Id
    private String id; // 채팅 식별 번호
    private String message;
    private String sender; // 송신자
    private String receiver; // 수신자
    private String roomNum; // 방 번호
    private boolean read; // 읽기 여부
    private LocalDateTime createdAt; // 전송 시각
}

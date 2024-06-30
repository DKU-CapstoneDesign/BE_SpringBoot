package project.capstone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "chat_room_members")
public class ChatRoomMembers {

    @EmbeddedId
    private ChatRoomMembersId id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable=false, updatable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", insertable=false, updatable=false)
    private ChatRoom chatRoom;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;
}

@Embeddable
class ChatRoomMembersId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "room_id")
    private Long roomId;
}
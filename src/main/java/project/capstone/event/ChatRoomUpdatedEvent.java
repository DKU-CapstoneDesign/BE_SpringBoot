package project.capstone.event;

import lombok.Getter;
import project.capstone.entity.ChatMessage;
import project.capstone.entity.ChatRoom;

@Getter
public class ChatRoomUpdatedEvent {
    private final ChatRoom chatRoom;
    private final ChatMessage chatMessage;

    public ChatRoomUpdatedEvent(ChatRoom chatRoom, ChatMessage chatMessage) {
        this.chatRoom = chatRoom;
        this.chatMessage = chatMessage;
    }

}
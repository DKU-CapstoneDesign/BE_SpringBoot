package project.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.capstone.entity.ChatMessage;
import project.capstone.entity.ChatRoomMembers;
import project.capstone.entity.User;
import project.capstone.repository.ChatMessageRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final ChatRoomMembersService chatRoomMembersService;

    public Mono<ChatMessage> save(ChatMessage chatMessage){
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessage.setRead(false);

        // sender와 receiver가 속한 채팅방을 찾아서 그 채팅방의 roomNum을 메시지의 roomNum에 설정
        User sender = userService.findByNickname(chatMessage.getSender());
        User receiver = userService.findByNickname(chatMessage.getReceiver());

        List<ChatRoomMembers> senderRooms = chatRoomMembersService.findByUserId(sender.getId());
        List<ChatRoomMembers> receiverRooms = chatRoomMembersService.findByUserId(receiver.getId());

        // TODO: 개선 필요
        for (ChatRoomMembers senderRoom : senderRooms) {
            for (ChatRoomMembers receiverRoom : receiverRooms) {
                if (senderRoom.getChatRoom().getId().equals(receiverRoom.getChatRoom().getId())) {
                    chatMessage.setRoomNum(senderRoom.getChatRoom().getId().toString());
                    break;
                }
            }
        }

        return chatMessageRepository.save(chatMessage);
    }

    public Flux<ChatMessage> findBySender(String sender, String receiver) {
        return chatMessageRepository.findBySenderAndReceiver(sender, receiver);
    }


    public Flux<ChatMessage> findByRoomNum(String roomNum) {
        return chatMessageRepository.findByRoomNum(roomNum);
    }

}

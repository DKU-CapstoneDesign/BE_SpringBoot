package project.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.capstone.entity.ChatMessage;
import project.capstone.entity.ChatRoom;
import project.capstone.entity.User;
import project.capstone.repository.ChatMessageRepository;
import project.capstone.repository.ChatRoomMembersRepository;
import project.capstone.repository.ChatRoomRepository;
import project.capstone.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMembersRepository chatRoomMembersRepository;
    private final UserRepository userRepository;

    /*// nickname -> User
        Set<User> members = new HashSet<>();
        members.add(userRepository.findByNickname(chatMessage.getSender()).orElseThrow());
        members.add(userRepository.findByNickname(chatMessage.getReceiver()).orElseThrow());

        // User id -> ChatRoomMembers -> ChatRoom
        ChatRoom chatRoom = chatRoomMembersRepository.findByMembers(members).orElseThrow();

        chatMessage.setRoomNum(String.valueOf(chatRoom.getId()));*/

    public Mono<ChatMessage> save(ChatMessage chatMessage){
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessage.setRead(false);

        return chatMessageRepository.save(chatMessage);
    }

    public Flux<ChatMessage> findBySender(String sender, String receiver) {
        return chatMessageRepository.findBySender(sender, receiver);
    }


    public Flux<ChatMessage> findByRoomNum(String roomNum) {
        return chatMessageRepository.findByRoomNum(roomNum);
    }

}

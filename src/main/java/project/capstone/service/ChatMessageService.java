package project.capstone.service;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import project.capstone.controller.ChatController;
import project.capstone.entity.ChatMessage;
import project.capstone.entity.ChatRoom;
import project.capstone.entity.ChatRoomMembers;
import project.capstone.entity.User;
import project.capstone.event.ChatRoomUpdatedEvent;
import project.capstone.repository.ChatMessageRepository;
import project.capstone.repository.ChatRoomRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ReactiveMongoTemplate mongoTemplate;

    private final ApplicationEventPublisher eventPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final ChatRoomMembersService chatRoomMembersService;

    public Flux<ChatMessage> findBySender(String sender, String receiver) {
        return chatMessageRepository.findBySenderAndReceiver(sender, receiver);
    }

    public Flux<ChatMessage> findByRoomNum(String roomNum) {
        return chatMessageRepository.findByRoomNum(roomNum);
    }

    public Flux<ChatMessage> findFirstByRoomNumOrderByCreatedAtDesc(String roomNum){
        return chatMessageRepository.findFirstByRoomNumOrderByCreatedAtDesc(roomNum);
    }

    // 메세지 읽음 표시
    public Mono<UpdateResult> updateRead(String roomNum, String nickname) {
        Query query = new Query(Criteria.where("roomNum").is(roomNum)
                .and("receiver").is(nickname));
        Update update = Update.update("read", true);

        return mongoTemplate.updateMulti(query, update, ChatMessage.class);
    }

    public Mono<Boolean> existsByRoomNumAndReceiverAndReadFalse(String roomNum, String nickname) {
        return chatMessageRepository.existsByRoomNumAndReceiverAndReadFalse(roomNum, nickname);
    }

    public Mono<ChatMessage> save(ChatMessage chatMessage){
        // 메시지 보낼 때 ChatRoom id를 메시지 roomNum 에 세팅해서 보내기
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

        return chatMessageRepository.save(chatMessage)
                .doOnNext(savedMessage -> {
                    // 이벤트 발생
                    ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(chatMessage.getRoomNum())).orElseThrow();
                    eventPublisher.publishEvent(new ChatRoomUpdatedEvent(chatRoom, chatMessage));
                });
    }

}

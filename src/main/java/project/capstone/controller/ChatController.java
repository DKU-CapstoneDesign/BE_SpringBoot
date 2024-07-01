package project.capstone.controller;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.capstone.dto.CreateChatRoomByNickname;
import project.capstone.dto.Read;
import project.capstone.entity.ChatMessage;
import project.capstone.entity.ChatRoom;
import project.capstone.entity.ChatRoomMembers;
import project.capstone.entity.User;
import project.capstone.event.ChatRoomUpdatedEvent;
import project.capstone.service.ChatMessageService;
import project.capstone.service.ChatRoomMembersService;
import project.capstone.service.ChatRoomService;
import project.capstone.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomMembersService chatRoomMembersService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final Sinks.Many<ChatRoom> chatRoomSink = Sinks.many().multicast().onBackpressureBuffer();

    // 대화 기록 가져오기 (송수신자 닉네임 기반)
    @CrossOrigin
    @GetMapping(value = "/api/sender/{sender}/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatMessage> getMessageBySender(@PathVariable String sender, @PathVariable String receiver) {
        return chatMessageService.findBySender(sender, receiver)
                .subscribeOn(Schedulers.boundedElastic());
    }

    // 대화 기록 가져오기 (채팅방 번호 기반)
    @CrossOrigin
    @GetMapping(value = "/api/chat/roomNum/{roomNum}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatMessage> getMessageByRoomId(@PathVariable String roomNum) {
        return chatMessageService.findByRoomNum(roomNum)
                .subscribeOn(Schedulers.boundedElastic());
    }

    // 메시지 읽음 요청
    @CrossOrigin
    @PutMapping("/api/chat/{roomNum}/user/{nickname}")
    public Mono<UpdateResult> updateIsRead(@PathVariable String roomNum, @PathVariable String nickname){
        log.info("request: read message");

        return chatMessageService.updateRead(roomNum, nickname);
    }

    // 메시지 보내기
    @CrossOrigin
    @PostMapping("/api/chat")
    public Mono<ChatMessage> setMessage(@RequestBody ChatMessage chatMessage){
        return chatMessageService.save(chatMessage);
    }

    // 채팅방 생성
    @PostMapping("/api/chat/creating")
    public ChatRoom createChatRoom(@RequestBody CreateChatRoomByNickname createChatRoomByNickname) {
        Set<User> userSet = new HashSet<>();
        ChatRoom chatRoom = null;

        for(User dto : createChatRoomByNickname.getMembers()){

            // nickname -> User
            User user = userService.findByNickname(dto.getNickname());
            userSet.add(user);

            // User -> ChatRoomMembers
            List<ChatRoomMembers> chatRoomMembersList = chatRoomMembersService.findByUserId(user.getId());

            // 이전 채팅방 기록이 존재 하는지 확인
            for(ChatRoomMembers chatRoomMembers : chatRoomMembersList){
                ChatRoom existingChatRoom = chatRoomMembers.getChatRoom();
                Set<User> existingMembers = existingChatRoom.getMembers();

                if(existingMembers.equals(userSet)){
                    chatRoom = existingChatRoom;
                    break;
                }
            }

            if(chatRoom != null){
                break;
            }
        }

        // 이전 채팅방 기록이 존재 하지 않는 경우
        if(chatRoom == null){
            // 새로운 채팅방 만들기
            chatRoom = new ChatRoom();
            chatRoom.setMembers(userSet);

            // 마지막 채팅 메시지 null 로 초기화
            chatRoom.setLastMessage(null);
            chatRoom.setUpdatedAt(null);

            // 채팅방 생성 일자 맞추기
            chatRoom.setCreatedAt(LocalDateTime.now());

            chatRoomService.save(chatRoom);
        }

        return chatRoom;
    }

    // 검색 닉네임이 포함된 채팅방 모두 가져오기
    // 채팅방을 찾고, 채팅방의 마지막 메시지를 가져와서 세팅해주기
    @GetMapping(value = "/api/chat/list/nickname/{nickname}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatRoom> getRoomsByNickname(@PathVariable String nickname){

        // nickname -> User -> userId
        Long userId = userService.findByNickname(nickname).getId();

        // 채팅방 마지막 메시지 세팅
        List<ChatRoomMembers> chatRoomMembers = chatRoomMembersService.findByUserId(userId);
        chatRoomMembers.forEach(chatRoomMember -> {
            ChatRoom chatRoom = chatRoomMember.getChatRoom();
            ChatMessage lastMessage = chatMessageService.findFirstByRoomNumOrderByCreatedAtDesc(chatRoom.getId().toString()).blockFirst();
            if (lastMessage != null) {
                chatRoom.setLastMessage(lastMessage.getMessage());
            }
        });

        Flux<ChatRoom> initialData = Flux.fromIterable(chatRoomMembers).map(ChatRoomMembers::getChatRoom);
        Flux<ChatRoom> updates = chatRoomSink.asFlux()
                .filter(chatRoom -> chatRoomMembers.stream()
                        .anyMatch(chatRoomMember -> chatRoomMember.getChatRoom().getId().equals(chatRoom.getId())));
        return Flux.concat(initialData, updates);
    }

    @EventListener
    public void handleChatRoomUpdatedEvent(ChatRoomUpdatedEvent event) {
        ChatRoom chatRoom = event.getChatRoom();
        chatRoom.setLastMessage(event.getChatMessage().getMessage());
        chatRoomService.save(chatRoom);
        chatRoomSink.tryEmitNext(chatRoom);
    }

    // 채팅방 별 읽음 여부 가져오기
    @GetMapping(value = "/api/chat/read/nickname/{nickname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Read>> getReadsByNickname(@PathVariable String nickname){
        User user = userService.findByNickname(nickname);
        List<ChatRoomMembers> chatRoomMembersList = chatRoomMembersService.findAllByUser(user);
        List<Read> readList = new ArrayList<>();

        for (ChatRoomMembers chatRoomMembers : chatRoomMembersList) {
            boolean hasUnreadMessage = chatMessageService.existsByRoomNumAndReceiverAndReadFalse(chatRoomMembers.getChatRoom().getId().toString(), nickname).block();
            chatRoomMembers.setRead(!hasUnreadMessage);
            chatRoomMembersService.save(chatRoomMembers);
            readList.add(new Read(chatRoomMembers.getChatRoom().getId().toString(), chatRoomMembers.isRead()));
        }
        return ResponseEntity.ok(readList);
    }

}

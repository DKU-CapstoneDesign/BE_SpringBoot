package project.capstone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import project.capstone.dto.CreateChatRoomByNickname;
import project.capstone.dto.FindChatRoomByNickname;
import project.capstone.entity.ChatMessage;
import project.capstone.entity.ChatRoom;
import project.capstone.entity.ChatRoomMembers;
import project.capstone.entity.User;
import project.capstone.repository.ChatMessageRepository;
import project.capstone.service.ChatMessageService;
import project.capstone.service.ChatRoomMembersService;
import project.capstone.service.ChatRoomService;
import project.capstone.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
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
    private final ChatMessageRepository chatMessageRepository;

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

    // 메시지 보내기
    @CrossOrigin
    @PostMapping("/api/chat")
    public Mono<ChatMessage> setMessage(@RequestBody ChatMessage chatMessage){
        // 메시지 보낼 때 ChatRoom id를 메시지 roomNum 에 세팅해서 보내기
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

            // 채팅방 생성 일자 맞추기
            chatRoom.setCreatedAt(LocalDateTime.now());

            chatRoomService.save(chatRoom);
        }

        return chatRoom;
    }


    // 검색 닉네임이 포함된 채팅방 모두 가져오기
    // 채팅방을 찾고, 채팅방의 마지막 메시지를 가져와서 세팅해주기
    // TODO: lastmessage 세팅하기 (실시간으로 업데이트 되어야 함)
    // TODO: read 가 false 인 채팅 메시지가 있는 채팅방 리스트 뽑기
    @PostMapping(value = "/api/chat/nickname", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatRoom> getRoomsByNickname(@RequestBody FindChatRoomByNickname findChatRoomByNickname){
        // nickname -> User(ID)
        Long userId = userService.findByNickname(findChatRoomByNickname.getNickname()).getId();

        // User(ID) -> ChatRoomMembers
        List<ChatRoomMembers> chatRoomMembers = chatRoomMembersService.findByUserId(userId);

        return Flux.fromIterable(chatRoomMembers)
                .flatMap(member -> {
                    ChatRoom chatRoom = member.getChatRoom();
                    // 채팅방 마지막 메시지 가져오기
                    return chatMessageRepository.findFirstByRoomNumOrderByCreatedAtDesc(chatRoom.getId().toString())
                            .doOnNext(lastMessage -> {
                                log.info("lastMessage: {}", lastMessage);
                                chatRoom.setLastMessage(lastMessage.getMessage());
                            })
                            .thenReturn(chatRoom);
                });
    }

    //TODO: @DeleteMapping("/api/chat/deleting/{roomNum}")

}

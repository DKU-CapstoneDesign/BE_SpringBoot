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
import project.capstone.repository.ChatRoomRepository;
import project.capstone.service.ChatMessageService;
import project.capstone.service.ChatRoomMembersService;
import project.capstone.service.ChatRoomService;
import project.capstone.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomMembersService chatRoomMembersService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    @CrossOrigin
    @GetMapping(value = "/api/sender/{sender}/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatMessage> getMessageBySender(@PathVariable String sender, @PathVariable String receiver) {
        return chatMessageService.findBySender(sender, receiver)
                .subscribeOn(Schedulers.boundedElastic());
    }

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
        // 목표 대상과의 채팅방이 이미 존재한다면 그 채팅방을 리턴하기
        /*System.out.println("createChatRoomByNickname: " + createChatRoomByNickname.getMembers());
        //ChatRoom existedChatRoom = chatRoomMembersService.findByMembers(createChatRoomByNickname.getMembers()).orElse(null);
        System.out.println("existedChatRoom: " + existedChatRoom);
        if(existedChatRoom != null){
            return existedChatRoom;
        }
        System.out.println("if pass");*/
        ChatRoom chatRoom = new ChatRoom();
        Set<User> userSet = new HashSet<>();

        // nickname -> User
        for(User dto : createChatRoomByNickname.getMembers()){
            userSet.add(userService.findByNickname(dto.getNickname()));
        }

        chatRoom.setMembers(userSet);

        // 마지막 채팅 메시지 null 로 초기화
        chatRoom.setLastMessage(null);

        // 채팅방 생성 일자 맞추기
        chatRoom.setCreatedAt(LocalDateTime.now());

        chatRoomService.save(chatRoom);

        return chatRoom;
    }


    // 검색 닉네임이 포함된 채팅방 모두 가져오기
    // TODO: lastmessage 세팅하기
    // TODO: read 가 false 인 채팅 메시지가 있는 채팅방 리스트 뽑기
    @PostMapping("/api/chat/nickname")
    public List<ChatRoom> getRoomsByNickname(@RequestBody FindChatRoomByNickname findChatRoomByNickname){

        Long userId = userService.findByNickname(findChatRoomByNickname.getNickname()).getId();
        List<ChatRoomMembers> chatRoomMembers = chatRoomMembersService.findByUserId(userId);

        List<ChatRoom> roomIdList = new ArrayList<>();
        for(ChatRoomMembers chatRoomMember : chatRoomMembers){
            roomIdList.add(chatRoomMember.getChatRoom());
        }

        return roomIdList;
    }
}

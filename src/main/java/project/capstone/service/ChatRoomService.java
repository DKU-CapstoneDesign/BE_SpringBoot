package project.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.capstone.entity.ChatRoom;
import project.capstone.entity.User;
import project.capstone.repository.ChatRoomRepository;

import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }


}

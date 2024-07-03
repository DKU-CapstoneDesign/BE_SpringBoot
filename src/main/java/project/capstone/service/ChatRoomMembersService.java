package project.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.capstone.entity.ChatRoomMembers;
import project.capstone.entity.User;
import project.capstone.repository.ChatRoomMembersRepository;

import java.util.List;


@RequiredArgsConstructor
@Service
public class ChatRoomMembersService {
    private final ChatRoomMembersRepository chatRoomMembersRepository;

    public List<ChatRoomMembers> findByUserId(Long userId){
        return chatRoomMembersRepository.findByUserId(userId).orElseThrow();
    }

    public ChatRoomMembers findByUser(User user) {
        return chatRoomMembersRepository.findByUser(user).orElse(null);
    }

    public List<ChatRoomMembers> findAllByUser(User user){
        return chatRoomMembersRepository.findAllByUser(user).orElseThrow();
    }

    public ChatRoomMembers findByIdUserIdAndIdRoomId(Long userId, Long roomId){
        return chatRoomMembersRepository.findByIdUserIdAndIdRoomId(userId, roomId).orElseThrow();
    }

    public ChatRoomMembers save(ChatRoomMembers chatRoomMembers){
        return chatRoomMembersRepository.save(chatRoomMembers);
    }

}

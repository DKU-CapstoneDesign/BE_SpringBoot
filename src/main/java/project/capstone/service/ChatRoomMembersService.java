package project.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.capstone.entity.ChatRoom;
import project.capstone.entity.ChatRoomMembers;
import project.capstone.entity.User;
import project.capstone.repository.ChatRoomMembersRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ChatRoomMembersService {
    private final ChatRoomMembersRepository chatRoomMembersRepository;

    public List<ChatRoomMembers> findByUserId(Long userId){
        return chatRoomMembersRepository.findByUserId(userId).orElseThrow();
    }

    /*public Optional<ChatRoom> findByUser(Set<User> members) {
        System.out.println("ChatRoomService.findByUser");
        return chatRoomMembersRepository.findByMembers(members);
    }*/

}

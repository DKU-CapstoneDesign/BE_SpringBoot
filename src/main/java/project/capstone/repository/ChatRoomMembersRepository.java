package project.capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.capstone.entity.ChatRoom;
import project.capstone.entity.ChatRoomMembers;
import project.capstone.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRoomMembersRepository extends JpaRepository<ChatRoomMembers, Long> {
    Optional<List<ChatRoomMembers>> findByUserId(Long userId);
    //Optional<ChatRoom> findByMembers(Set<User> members);
}

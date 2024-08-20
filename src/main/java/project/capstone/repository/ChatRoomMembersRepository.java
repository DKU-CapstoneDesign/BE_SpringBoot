package project.capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.capstone.entity.ChatRoomMembers;
import project.capstone.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMembersRepository extends JpaRepository<ChatRoomMembers, Long> {
    Optional<List<ChatRoomMembers>> findByUserId(Long userId);
    Optional<ChatRoomMembers> findByUser(User user);
    Optional<List<ChatRoomMembers>> findAllByUser(User user);
    Optional<ChatRoomMembers> findByIdUserIdAndIdRoomId(Long userId, Long roomId);
}

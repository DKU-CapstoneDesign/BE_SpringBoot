package project.capstone.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import project.capstone.entity.ChatMessage;
import reactor.core.publisher.Flux;

@Repository
public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String>{

    @Tailable // 커서를 닫지 않고 계속 유지한다.
    @Query("{ sender : ?0, receiver : ?1}")
    Flux<ChatMessage> findBySender(String sender, String receiver);

    @Tailable
    @Query("{ roomNum: ?0 }")
    Flux<ChatMessage> findByRoomNum(String roomNum);

}

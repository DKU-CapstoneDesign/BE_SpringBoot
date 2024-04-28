package project.capstone.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import project.capstone.entity.Chat;
import reactor.core.publisher.Flux;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String>{

    @Tailable // 커서를 안닫고 계속 유지한다.
    @Query("{ sender : ?0, receiver : ?1}")
    Flux<Chat> findBySender(String sender, String receiver);

    @Tailable
    @Query("{ roomNum: ?0 }")
    Flux<Chat> findByRoomNum(Long roomNum);
}

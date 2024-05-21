package project.capstone.service;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import project.capstone.entity.ChatRoom;
import project.capstone.repository.ChatRoomRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    private final ReactiveMongoTemplate mongoTemplate;

    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    public Mono<UpdateResult> updateTime(String roomNum){
        Query query = new Query(Criteria.where("roomNum").is(roomNum));
        Update update = Update.update("updatedAt", LocalDateTime.now());

        return mongoTemplate.updateFirst(query, update, ChatRoom.class);
    }

}

package project.capstone.dto;

import lombok.Getter;
import project.capstone.entity.User;

import java.util.Set;

@Getter
public class CreateChatRoomByNickname {
    private Set<User> members;
}

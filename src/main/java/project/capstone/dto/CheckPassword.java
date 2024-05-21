package project.capstone.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckPassword {
    private Long userId;
    private String password;
}

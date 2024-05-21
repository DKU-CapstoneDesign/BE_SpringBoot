package project.capstone.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyPassword {
    private Long userId;
    private String newPassword;
}

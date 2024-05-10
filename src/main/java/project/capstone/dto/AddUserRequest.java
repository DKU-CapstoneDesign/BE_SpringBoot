package project.capstone.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
    private String nickname;
    private String country;
    private String birthDate;
}

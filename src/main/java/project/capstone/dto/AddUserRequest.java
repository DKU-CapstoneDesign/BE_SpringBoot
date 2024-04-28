package project.capstone.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class AddUserRequest {
    private String email;
    private String password;
    private String country;
    private Date birthDate;
}

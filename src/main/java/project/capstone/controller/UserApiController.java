package project.capstone.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.capstone.dto.*;
import project.capstone.entity.User;
import project.capstone.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/api/signup")
    public Signup signup(AddUserRequest request) {
        Signup signup = new Signup();
        signup.setId(userService.save(request));
        return signup;
    }

    @GetMapping("/api/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    }

    @PostMapping("/api/duplication/email")
    public Duplication checkEmail(@RequestBody CheckUserEmail emailDto){
        log.debug("emailDto: {}", emailDto.getEmail());
        Duplication duplication = new Duplication();
        User user = userService.findByEmail(emailDto.getEmail());
        duplication.setDuplication(user != null);
        log.debug("duplication: {}", duplication.isDuplication());
        return duplication;
    }

    @PostMapping("/api/duplication/nickname")
    public Duplication checkNickname(@RequestBody CheckUserNickname nicknameDto){
        log.debug("nicknameDto: {}", nicknameDto.getNickname());
        Duplication duplication = new Duplication();
        User user = userService.findByNickname(nicknameDto.getNickname());
        duplication.setDuplication(user != null);
        log.debug("duplication: {}", duplication.isDuplication());
        return duplication;
    }
}

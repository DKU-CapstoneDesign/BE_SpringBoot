package project.capstone.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/api/check/password")
    public boolean checkCurrentPassword(@RequestBody CheckPassword request){
        log.debug("request: check current password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(request.getPassword(), userService.findById(request.getUserId()).getPassword());
    }

    @PutMapping("/api/modify/password")
    public User modifyPassword(@RequestBody ModifyPassword request){
        log.debug("request: modify password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = userService.findById(request.getUserId());
        user.setPassword(encoder.encode(request.getNewPassword()));
        userService.save(user);
        return user;
    }
}

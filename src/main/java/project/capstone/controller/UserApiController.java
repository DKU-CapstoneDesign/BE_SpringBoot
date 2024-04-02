package project.capstone.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.capstone.domain.User;
import project.capstone.dto.AddUserRequest;
import project.capstone.dto.CheckUserEmail;
import project.capstone.dto.CheckUserNickname;
import project.capstone.dto.Duplication;
import project.capstone.service.UserDetailService;
import project.capstone.service.UserService;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;
    private final UserDetailService userDetailService;
    @PostMapping("/api/signup")
    public String signup(AddUserRequest request) {
        userService.save(request);
        return "redirect:/login";
    }

    @GetMapping("/api/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

    @ResponseBody
    @PostMapping("/api/duplication/email")
    public Duplication checkEmail(@RequestBody CheckUserEmail emailDto){
        Duplication duplication = new Duplication();
        User user = userService.findByEmail(emailDto.getEmail());
        if(user == null){
            duplication.setDuplication(false);
        }
        else{
            duplication.setDuplication(true);
        }
        return duplication;

    }

    @ResponseBody
    @PostMapping("/api/duplication/nickname")
    public Duplication checkNickname(@RequestBody CheckUserNickname nicknameDto){
        Duplication duplication = new Duplication();
        User user = userService.findByNickname(nicknameDto.getNickname());
        if(user == null){
            duplication.setDuplication(false);
        }
        else{
            duplication.setDuplication(true);
        }
        return duplication;
    }
}

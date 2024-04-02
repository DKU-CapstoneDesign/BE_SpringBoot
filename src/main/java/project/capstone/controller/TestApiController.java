package project.capstone.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import project.capstone.domain.User;
import project.capstone.dto.AddUserRequest;
import project.capstone.dto.TextDTO;
import project.capstone.service.TextService;
import project.capstone.service.UserService;

@Controller
@RequiredArgsConstructor
public class TestApiController {
    // 생성자 주입
    private final UserService userService;
    private final TextService textService;

    @PostMapping("/app/login")
    public String login(@ModelAttribute AddUserRequest addUserRequestDTO, HttpSession session) {
        String typedEmail = addUserRequestDTO.getEmail();
        String typedPassword = addUserRequestDTO.getPassword();
        System.out.println("email : " + typedEmail);

        try {
            User byEmail = userService.findByEmail(typedEmail);
            System.out.println("byEmail: " + byEmail.getEmail());
            String pw = byEmail.getPassword();

            if (typedPassword.equals(pw)) {
                System.out.println("로그인 성공");
                session.setAttribute("loginEmail", byEmail.getEmail());

                return "home";
            } else {
                System.out.println("비밀번호 오류");
                System.out.println("입력: " + typedPassword + ", pw: " + pw);


                return "login";
            }
        } catch (IllegalArgumentException e) {
            System.out.println("error = " + e);
            return "login";
        }
    }


    @GetMapping("/app/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @GetMapping("/app/community/write-text")
    public String writeText() {
        return "write_text";
    }

    @PostMapping("app/community/write-text")
    public String saveText(HttpSession session, @ModelAttribute TextDTO textDTO) {
        String textWriter = (String) session.getAttribute("loginEmail");
        System.out.println(textWriter);
        textDTO.setTextWriter(textWriter);
        System.out.println("session = " + session + ", textDTO = " + textDTO);

        textService.save(textDTO);


        return "community";
    }

}

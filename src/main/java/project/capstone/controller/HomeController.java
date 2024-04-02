package project.capstone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/index")
    public String toIndexPage() {
        return "index";
    }

    @GetMapping("/app/login")
    public String toLoginPage() {
        return "login";
    }

    @GetMapping("/app/home")
    public String toHomePage() {
        return "home";
    }

    @GetMapping("/app/guide")
    public String toGuidePage() {
        return "guide";
    }

    @GetMapping("/app/chatting")
    public String toChattingPage() {
        return "chatting";
    }

    @GetMapping("/app/community")
    public String toCommunityPage() {
        return "community";
    }

    @GetMapping("/app/mypage")
    public String toMypagePage() {
        return "mypage";
    }
}

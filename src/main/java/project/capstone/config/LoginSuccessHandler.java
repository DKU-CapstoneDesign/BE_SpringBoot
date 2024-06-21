package project.capstone.config;

import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import project.capstone.dto.Login;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @CrossOrigin
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json");
        log.info("request: /api/login");
        Login login = new Login();
        login.setLoggedIn(authentication.getName() != null);
        login.setAuthentication(authentication);

        response.setHeader("Access-Control-Allow-Origin", "*");

        Gson gson = new Gson();
        String jsonData = gson.toJson(login);

        log.info("jsonData: {}", jsonData);

        PrintWriter out = response.getWriter();
        out.println(jsonData);
    }
}

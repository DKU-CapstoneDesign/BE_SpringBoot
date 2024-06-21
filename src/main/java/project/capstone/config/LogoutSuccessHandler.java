package project.capstone.config;

import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import project.capstone.dto.Logout;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json");
        log.info("request: /api/logout");

        Logout logout = new Logout();
        logout.setLoggedOut(true);

        Gson gson = new Gson();
        String jsonData = gson.toJson(logout);

        PrintWriter out = response.getWriter();
        out.println(jsonData);
    }
}

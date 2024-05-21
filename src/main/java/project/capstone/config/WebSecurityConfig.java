package project.capstone.config;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import project.capstone.dto.Login;
import project.capstone.dto.Logout;
import project.capstone.service.UserDetailService;

import java.io.PrintWriter;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeRequests()
                .requestMatchers("/api/**", "/signup", "/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginProcessingUrl("/api/login")
                .successHandler(
                        (request, response, authentication) -> {
                            response.setContentType("application/json");

                            Login login = new Login();
                            login.setLoggedIn(authentication.getName() != null);
                            login.setAuthentication(authentication);

                            Gson gson = new Gson();
                            String jsonData = gson.toJson(login);

                            PrintWriter out = response.getWriter();
                            out.println(jsonData);
                        })
                .and()
                .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(
                        (request, response, authentication) -> {
                            response.setContentType("application/json");

                            Logout logout = new Logout();
                            logout.setLoggedOut(true);

                            Gson gson = new Gson();
                            String jsonData = gson.toJson(logout);

                            PrintWriter out = response.getWriter();
                            out.println(jsonData);
                        }
                        )
                .invalidateHttpSession(true)
                .and()
                .csrf().disable()
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

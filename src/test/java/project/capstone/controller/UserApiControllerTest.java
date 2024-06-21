package project.capstone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import project.capstone.entity.User;
import project.capstone.repository.UserRepository;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {

        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("user")
                .nickname("user")
                .country("Korea")
                .birthDate("1999-01-01")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    @DisplayName("signUp: 새로운 User 추가에 성공한다.")
    @Test
    public void signUp() throws Exception {
        // given
        final String url = "/api/signup";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        final String email = "test@gmail.com";
        final String password = "test";
        final String nickname = "test";
        final String country = "Korea";
        final String birthDate = "1999-01-01";

        final User user = userRepository.save(User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .country(country)
                .birthDate(birthDate)
                .build());

        final String requestBody = objectMapper.writeValueAsString(user);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));

        // then
        result.andExpect(status().isCreated());

        List<User> userList = userRepository.findAll();

        assertThat(userList.size()).isEqualTo(1);
        assertThat(userList.get(0).getEmail()).isEqualTo(email);
        assertThat(userList.get(0).getNickname()).isEqualTo(nickname);
        assertThat(userList.get(0).getCountry()).isEqualTo(country);
        assertThat(userList.get(0).getBirthDate()).isEqualTo(birthDate);
    }


}

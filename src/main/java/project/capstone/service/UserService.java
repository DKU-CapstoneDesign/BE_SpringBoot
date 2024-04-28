package project.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import project.capstone.entity.User;
import project.capstone.dto.AddUserRequest;
import project.capstone.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public Long save(AddUserRequest dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(
                User
                        .builder()
                        .email(dto.getEmail())
                        .password(encoder.encode(dto.getPassword()))
                        .country(dto.getCountry())
                        .birthDate(dto.getBirthDate())
                        .build()
                )
                .getId();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByNickname(String nickname) {
        return userRepository.findByNickname(nickname).orElse(null);
    }
}

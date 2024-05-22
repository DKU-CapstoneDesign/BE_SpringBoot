package project.capstone.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.capstone.entity.User;
import project.capstone.entity.enumSet.ErrorType;
import project.capstone.repository.UserRepository;

@Service    // custom 하고 Bean 으로 등록 후 사용 가능
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorType.NOT_FOUND_USER.getMessage()));   // 사용자가 DB 에 없으면 예외처리

        return new UserDetailsImpl(user, user.getUsername());   // 사용자 정보를 UserDetails 로 반환
    }
}
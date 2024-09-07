package project.capstone.security;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.capstone.entity.User;
import project.capstone.entity.enumSet.UserRoleEnum;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    @Getter
    private final User user;

    // 인증이 완료된 사용자 추가하기
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = user.getRole();
        String authority = role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();   // 사용자 권한을 GrantedAuthority 로 추상화
        authorities.add(simpleGrantedAuthority);

        return authorities; // GrantedAuthority 로 추상화된 사용자 권한 반환
    }

    @Override
    public String getPassword() {
        return user.getPassword();  // 사용자 비밀번호 반환
    }

    @Override
    public String getUsername() {
        return user.getUsername();  // 사용자 이름 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정이 만료되지 않음을 나타냄
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정이 잠기지 않음을 나타냄
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 자격 증명이 만료되지 않음을 나타냄
    }

    @Override
    public boolean isEnabled() {
        return true;  // 계정이 활성화되어 있음을 나타냄
    }
}
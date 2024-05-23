package project.capstone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.capstone.entity.enumSet.UserRoleEnum;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "country")
    private String country;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "role", nullable = false, length = 30)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Builder
    public User(String email, String password, String nickname, String country, String birthDate) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.country = country;
        this.birthDate = birthDate;
    }


    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

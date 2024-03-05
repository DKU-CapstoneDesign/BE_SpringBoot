package proejct.capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proejct.capstone.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

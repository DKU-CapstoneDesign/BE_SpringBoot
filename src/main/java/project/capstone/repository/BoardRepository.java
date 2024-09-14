package project.capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.capstone.entity.Board;
import project.capstone.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    // 수정 날짜로 내림차순 정렬
    List<Board> findAllByOrderByModifiedAtDesc();

    // 생성 날짜로 내림차순 정렬
    List<Board> findAllByOrderByCreatedAtDesc();

    Optional<Board> findByIdAndUser(Long id, User user);

    void deleteAllByUser(User user);
}
package project.capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.capstone.entity.Attachment;
import project.capstone.entity.Board;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByBoard(Board board);
    // 추가적인 쿼리 메서드가 필요한 경우 여기에 정의할 수 있습니다.
}
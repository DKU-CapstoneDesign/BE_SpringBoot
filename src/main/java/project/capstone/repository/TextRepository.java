package project.capstone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.capstone.entity.TextEntity;

public interface TextRepository extends JpaRepository<TextEntity, Long> {


}

package project.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.capstone.dto.TextDTO;
import project.capstone.entity.TextEntity;
import project.capstone.repository.TextRepository;

@Service
@RequiredArgsConstructor
public class TextService {
    // 생성자 주입
    private final TextRepository textRepository;


    public void save(TextDTO textDTO) {
        TextEntity textEntity = TextEntity.toTextEntity(textDTO);
        textRepository.save(textEntity);
    }
}

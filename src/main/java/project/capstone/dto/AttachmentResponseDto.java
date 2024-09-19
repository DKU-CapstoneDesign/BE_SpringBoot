package project.capstone.dto;

import lombok.Builder;
import lombok.Getter;
import project.capstone.entity.Attachment;

@Getter
public class AttachmentResponseDto {
    private String fileName;  // 첨부파일의 이름
    private String filePath;  // 첨부파일의 파일 경로

    @Builder
    public AttachmentResponseDto(String fileName, String filePath) {

        this.fileName = fileName;
        this.filePath = filePath;
    }

    // Entity를 DTO로 변환하는 정적 메서드
    public static AttachmentResponseDto from(Attachment attachment) {
        return AttachmentResponseDto.builder()
                .fileName(attachment.getFileName())
                .filePath(attachment.getFilePath())
                .build();
    }
}

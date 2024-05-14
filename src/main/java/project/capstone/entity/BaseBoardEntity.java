package project.capstone.entity;


import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseBoardEntity {
    @CreationTimestamp
    @Column(updatable = false) // 수정시 관여 안함
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(insertable = false) // 입력시 관여 안함
    private LocalDateTime updatedTime;
}
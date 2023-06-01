package study.datajpa.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable= false) // createdDate 생성 이후 업데이트 되지 않도록
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    } // em.persist전 이벤트 발생

    @PreUpdate
    public void preUpdate(){
        updatedDate = LocalDateTime.now();
    }
}

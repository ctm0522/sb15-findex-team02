package org.findex.team2.sb15findexteam02.model;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class BaseUpdatebleModel extends BaseModel{
    @LastModifiedDate
    @Column
    protected LocalDateTime updatedAt;

    protected BaseUpdatebleModel(){

    }
}

package org.findex.team2.sb15findexteam02.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;



@Entity
@Table(name = "auto_sync_config")
@Getter
public class AutoSyncConfig extends BaseUpdatableModel{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id",nullable = false)
    private IndexInfo indexInfo;

    @Column(nullable = false)
    private boolean enabled;


    @Column
    private LocalDateTime lastSyncedAt;

    protected AutoSyncConfig(){}

    public AutoSyncConfig(IndexInfo indexInfo){
        this.indexInfo=indexInfo;
        this.enabled=false;
    }
}

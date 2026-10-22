package org.findex.team2.sb15findexteam02.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "sync_jobs")
@Entity
@Getter
public class SyncJob extends BaseModel{
    @Column(nullable = false)
    private LocalDateTime jobTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ResultType status;

    @Column(length = 50, nullable = false)
    private String worker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private JobType jobType;

    @Column
    private LocalDate targetDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id",nullable = false)
    private IndexInfo indexInfo;

    protected SyncJob(){}

    public SyncJob(ResultType status, String worker, JobType jobType, LocalDate targetDate,
                   IndexInfo IndexInfo){
        this.jobTime=LocalDateTime.now();
        this.status=status;
        this.worker=worker;
        this.jobType=jobType;
        this.targetDate=targetDate;
        this.indexInfo=IndexInfo;

    }

}

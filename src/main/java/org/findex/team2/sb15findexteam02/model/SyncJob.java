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
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private Integer totalCount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sync_job_index_infos",
            joinColumns = @JoinColumn(name = "sync_jobs_id"),
            inverseJoinColumns = @JoinColumn(name = "index_info_id")
    )
    private List<IndexInfo> infos = new ArrayList<>();

}

package org.findex.team2.sb15findexteam02.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.findex.team2.sb15findexteam02.model.JobType;
import org.findex.team2.sb15findexteam02.model.SyncJob;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyncJobDto {
    Long id;
    JobType jobType;
    Long indexInfoId;
    LocalDate targetDate;
    String worker;
    LocalDateTime jobTime;
    String result;

    public static SyncJobDto from(SyncJob syncJob) {
        return new SyncJobDto(
                syncJob.getId(),
                syncJob.getJobType(),
                syncJob.getIndexInfo().getId(),
                syncJob.getTargetDate(),
                syncJob.getWorker(),
                syncJob.getJobTime(),
                syncJob.getStatus().name()
        );
    }
}

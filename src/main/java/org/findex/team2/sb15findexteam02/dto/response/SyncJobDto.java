package org.findex.team2.sb15findexteam02.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.findex.team2.sb15findexteam02.model.JobType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
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
}

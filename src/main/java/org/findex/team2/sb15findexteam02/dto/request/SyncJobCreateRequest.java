package org.findex.team2.sb15findexteam02.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.findex.team2.sb15findexteam02.model.JobType;
import org.findex.team2.sb15findexteam02.model.ResultType;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SyncJobCreateRequest {
    ResultType status;
    String worker;
    JobType jobType;
    LocalDate startDate;
    LocalDate endDate;
    Integer totalCount;
}

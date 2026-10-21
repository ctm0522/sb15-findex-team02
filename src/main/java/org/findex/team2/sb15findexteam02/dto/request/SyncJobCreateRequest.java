package org.findex.team2.sb15findexteam02.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.findex.team2.sb15findexteam02.model.IndexInfo;
import org.findex.team2.sb15findexteam02.model.JobType;
import org.findex.team2.sb15findexteam02.model.ResultType;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SyncJobCreateRequest {
    ResultType status;
    String worker;
    JobType jobType;
    LocalDate targetDate;
    IndexInfo indexInfo;
}

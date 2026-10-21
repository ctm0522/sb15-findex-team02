package org.findex.team2.sb15findexteam02.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.findex.team2.sb15findexteam02.model.JobType;
import org.findex.team2.sb15findexteam02.model.ResultType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SyncjobSearchRequest {

    JobType jobType;
    Long indexInfoId;
    LocalDate baseDateFrom;
    LocalDate baseDateTo;
    String worker;
    LocalDateTime jobTimeFrom;
    LocalDateTime jobTimeTo;
    ResultType status;
    Long idAfter;
    String cursor;
    String sortField = "jobTime";
    String sortDirection = "desc";
    Integer size = 10;
}

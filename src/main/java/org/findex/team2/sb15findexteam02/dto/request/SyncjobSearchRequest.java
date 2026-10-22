package org.findex.team2.sb15findexteam02.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.findex.team2.sb15findexteam02.model.JobType;
import org.findex.team2.sb15findexteam02.model.ResultType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyncjobSearchRequest {

    JobType jobType;
    Long indexInfoId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate baseDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate baseDateTo;
    String worker;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime jobTimeFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime jobTimeTo;
    ResultType status;
    Long idAfter;
    String cursor;
    String sortField = "jobTime";
    String sortDirection = "desc";
    Integer size = 10;
}

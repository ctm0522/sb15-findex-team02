package org.findex.team2.sb15findexteam02.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.findex.team2.sb15findexteam02.model.JobType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SyncJobResponse {
  Long id;
  JobType jobType;
  Long indexInfoId;
  LocalDate targetDate;
  String worker;
  String jobTime;
  String result;
}

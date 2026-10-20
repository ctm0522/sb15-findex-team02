package org.findex.team2.sb15findexteam02.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexDateSyncRequest {
  List<String> indexInfoIds;
  LocalDate baseDateFrom;
  LocalDate baseDateTo;
}

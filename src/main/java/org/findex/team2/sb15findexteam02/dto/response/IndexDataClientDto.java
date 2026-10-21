package org.findex.team2.sb15findexteam02.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexDataClientDto {
  List<String> infoNames;
  List<String> categoryNames;
  List<Long> indexInfoIds;
  LocalDate baseDateFrom;
  LocalDate baseDateTo;
}

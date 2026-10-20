package org.findex.team2.sb15findexteam02.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexInfoDto {
  Integer id;
  String indexClassification;
  String indexName;
  Integer employedItemsCount;
  LocalDate basePointInTime;
  Long baseIndex;
  String sourceType;
  Boolean favorite;
}

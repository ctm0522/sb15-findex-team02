package org.findex.team2.sb15findexteam02.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexInfoCreateRequest {
  String indexClassification;
  String indexName;
  Integer employedItemsCount;
  LocalDate basePointInTime;
  BigDecimal baseIndex;
  Boolean favorite;
}

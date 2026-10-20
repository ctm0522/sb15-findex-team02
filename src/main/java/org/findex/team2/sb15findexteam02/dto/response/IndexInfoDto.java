package org.findex.team2.sb15findexteam02.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.findex.team2.sb15findexteam02.model.SourceType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexInfoDto {
  Long id;
  String indexClassification;
  String indexName;
  Integer employedItemsCount;
  LocalDate basePointInTime;
  BigDecimal baseIndex;
  SourceType sourceType;
  Boolean favorite;
}

package org.findex.team2.sb15findexteam02.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PublicDataInfoResponse {
  // 분류명
  @JsonProperty("idxCsf")
  String indexClassification;
  // 지수명
  @JsonProperty("idxNm")
  String indexName;
  // 기준 시점
  @JsonProperty("basDt")
  LocalDate basDate;
  // 채용 종목 수
  @JsonProperty("epyItmsCnt")
  Integer employedItemCount;
  // 기준 지수
  @JsonProperty("basIdx")
  BigDecimal baseIndex;


}

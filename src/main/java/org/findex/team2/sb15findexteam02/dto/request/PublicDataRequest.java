package org.findex.team2.sb15findexteam02.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PublicDataRequest {
  String resultType;
  String serviceKey;
  // 지수 정보를 가져올 때 필요한 기준일자
  LocalDate baseDate;
  // 지수 데이터를 가져올 때 필요한 기준일자
  LocalDate beginBaseDate;
  LocalDate endBaseDate;
  String idxNm;
}

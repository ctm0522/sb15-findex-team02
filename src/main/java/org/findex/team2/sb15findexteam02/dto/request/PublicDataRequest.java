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
  LocalDate beginBasDt;
  LocalDate endBasDt;
  String idxNm;
}

package org.findex.team2.sb15findexteam02.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicDataResponse {
  /*@JsonProperty("beginBasDt")
  LocalDate baseDateFrom;
  @JsonProperty("endBasDt")
  LocalDate baseDateTo;*/
  LocalDate baseDate;
  @JsonProperty("idxNm")
  List<String> infoNames;
  @JsonProperty("idxCsf")
  List<String> categoryNames;
  @JsonProperty("mkp")
  BigDecimal marketPrice;
  @JsonProperty("dpr")
  BigDecimal dailyPrice;
  @JsonProperty("hipr")
  BigDecimal highPrice;
  @JsonProperty("lopr")
  BigDecimal lowPrice;
  @JsonProperty("vs")
  BigDecimal vs;
  @JsonProperty("fltRt")
  BigDecimal fluctuationRate;
  @JsonProperty("trqu")
  Long tradingQuantity;
  @JsonProperty("trPrc")
  Long tradingPrice;
  @JsonProperty("lstgMrktTotAmt")
  Long marketTotalAmount;
}

package org.findex.team2.sb15findexteam02.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicDataResponse {
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

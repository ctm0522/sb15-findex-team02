package org.findex.team2.sb15findexteam02.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexDataDto {

    Long id;
    Long indexInfoId;
    LocalDate baseDate;
    String sourceType;
    BigDecimal marketPrice;
    BigDecimal closingPrice;
    BigDecimal highPrice;
    BigDecimal lowPrice;
    BigDecimal versus;
    BigDecimal fluctuationRate;
    Long tradingQuantity;
    Long tradingPrice;
    Long marketTotalAmount;
}
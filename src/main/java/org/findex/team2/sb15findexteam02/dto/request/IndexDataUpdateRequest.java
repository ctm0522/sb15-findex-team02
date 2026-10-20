package org.findex.team2.sb15findexteam02.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexDataUpdateRequest {

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
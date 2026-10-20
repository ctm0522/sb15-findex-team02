package org.findex.team2.sb15findexteam02.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexPerformanceDto {
    Long indexInfoId;
    String indexClassification;
    String indexName;
    BigDecimal versus;
    BigDecimal fluctuationRate;
    BigDecimal currentPrice;
    BigDecimal beforePrice;
}
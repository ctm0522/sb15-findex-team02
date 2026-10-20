package org.findex.team2.sb15findexteam02.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexChartDto {
    Long indexInfoId;
    String indexClassification;
    String indexName;
    String periodType;
    List<ChartDataPoint> dataPoints;
    List<ChartDataPoint> ma5DataPoints;
    List<ChartDataPoint> ma20DataPoints;
}
package org.findex.team2.sb15findexteam02.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.findex.team2.sb15findexteam02.model.ChartPeriodType;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexChartDto {
    Long indexInfoId;
    String indexClassification;
    String indexName;
    ChartPeriodType periodType;
    List<ChartDataPoint> dataPoints;
    List<ChartDataPoint> ma5DataPoints;
    List<ChartDataPoint> ma20DataPoints;
}

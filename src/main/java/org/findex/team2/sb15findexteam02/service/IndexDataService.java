package org.findex.team2.sb15findexteam02.service;

import org.findex.team2.sb15findexteam02.dto.request.IndexDataCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSearchRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.*;
import org.findex.team2.sb15findexteam02.model.IndexData;
import org.findex.team2.sb15findexteam02.model.PeriodType;
import org.findex.team2.sb15findexteam02.model.ChartPeriodType;
import org.findex.team2.sb15findexteam02.model.SourceType;

import java.util.List;

public interface IndexDataService {
    CursorPageResponseIndexDataDto findAll(IndexDataSearchRequest request);
    IndexDataDto create(IndexDataCreateRequest request, SourceType sourceType);
    void delete(Long id);
    IndexDataDto update(Long id, IndexDataUpdateRequest request, SourceType sourceType);
    IndexChartDto Chart(Long id, ChartPeriodType periodType);
    List<RankedIndexPerformanceDto> rank(Long id, PeriodType periodType, int limit);
    List<IndexPerformanceDto> favorite(PeriodType periodType);
    byte[] toCSV(IndexDataSearchRequest request);


}

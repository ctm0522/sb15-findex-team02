package org.findex.team2.sb15findexteam02.service.basic;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.CursorPageResponseIndexDataDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexChartDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexDataDto;
import org.findex.team2.sb15findexteam02.dto.response.RankedIndexPerformanceDto;
import org.findex.team2.sb15findexteam02.model.IndexData;
import org.findex.team2.sb15findexteam02.model.IndexInfo;
import org.findex.team2.sb15findexteam02.model.PeriodType;
import org.findex.team2.sb15findexteam02.model.SourceType;
import org.findex.team2.sb15findexteam02.repository.IndexCategoryRepository;
import org.findex.team2.sb15findexteam02.repository.IndexDataRepository;
import org.findex.team2.sb15findexteam02.repository.IndexInfoRepository;
import org.findex.team2.sb15findexteam02.service.IndexDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicIndexDataService implements IndexDataService {
    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexCategoryRepository indexCategoryRepository;


    @Override
    public CursorPageResponseIndexDataDto findAll(IndexDataSearchRequest request) {
        return null;
    }

    @Override
    @Transactional
    public IndexDataDto create(IndexDataCreateRequest request, SourceType sourceType) {
        IndexInfo indexInfo = indexInfoRepository.findById(request.getIndexInfoId()).orElseThrow(() -> new NoSuchElementException("해당 id의 지수를 찾을 수 없음 : "+ request.getIndexInfoId()));
        IndexData indexData = new IndexData(indexInfo,request.getBaseDate(),request.getMarketPrice(),request.getClosingPrice(),
                request.getHighPrice(),request.getLowPrice(),request.getVersus(),request.getFluctuationRate(),request.getTradingQuantity(),
                request.getTradingPrice(),request.getMarketTotalAmount(), sourceType);
        IndexData createIndexData = indexDataRepository.save(indexData);
        return IndexDataDto.from(createIndexData);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        indexDataRepository.deleteById(id);
    }

    @Override
    @Transactional
    public IndexDataDto update(Long id, IndexDataUpdateRequest request, SourceType sourceType) {
        IndexData indexData = indexDataRepository .findById(id).orElseThrow(() -> new NoSuchElementException("해당 id 데이터없음 : "+id));
        indexData.update(request.getMarketPrice(),request.getClosingPrice(),request.getHighPrice(),request.getLowPrice(),request.getVersus(),
                request.getFluctuationRate(),request.getTradingQuantity(),request.getTradingPrice(),request.getMarketTotalAmount(),sourceType);
        return IndexDataDto.from(indexData);
    }

    @Override
    public IndexChartDto Chart(Long id, PeriodType periodType) {

        return null;
    }

    @Override
    public List<RankedIndexPerformanceDto> rank(Long id, PeriodType periodType, int limit) {
        return List.of();
    }

    @Override
    public List<RankedIndexPerformanceDto> favorite(PeriodType periodType) {
        return List.of();
    }

    @Override
    public ResponseEntity<?> toCSV() {

        return null;
    }
}

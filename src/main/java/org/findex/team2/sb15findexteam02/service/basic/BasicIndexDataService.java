package org.findex.team2.sb15findexteam02.service.basic;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSearchRequest;
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
import org.findex.team2.sb15findexteam02.specification.IndexDataSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BasicIndexDataService implements IndexDataService {
    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexCategoryRepository indexCategoryRepository;


    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseIndexDataDto findAll(
            IndexDataSearchRequest request
    ) {

        // 1. 기본값 설정
        int size = request.getSize() == null
                ? 10
                : request.getSize();

        String sortField = request.getSortField() == null
                ? "baseDate"
                : request.getSortField();

        String sortDirection = request.getSortDirection() == null
                ? "desc"
                : request.getSortDirection().toLowerCase(Locale.ROOT);

        // 2. 유효성 검증
        if (size < 1) {
            throw new IllegalArgumentException(
                    "size는 1 이상이어야 합니다."
            );
        }

        Set<String> allowedFields = Set.of(
                "baseDate",
                "marketPrice",
                "closingPrice",
                "highPrice",
                "lowPrice",
                "versus",
                "fluctuationRate",
                "tradingQuantity",
                "tradingPrice",
                "marketTotalAmount"
        );

        if (!allowedFields.contains(sortField)) {
            throw new IllegalArgumentException(
                    "지원하지 않는 정렬 필드: " + sortField
            );
        }

        if (!sortDirection.equals("asc")
                && !sortDirection.equals("desc")) {
            throw new IllegalArgumentException(
                    "지원하지 않는 정렬 방향: " + sortDirection
            );
        }

        if (request.getStartDate() != null
                && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException(
                    "시작 날짜는 종료 날짜보다 늦을 수 없습니다."
            );
        }

        // 3. 검색 조건 생성
        Specification<IndexData> searchSpec =
                IndexDataSpecification.search(request);

        // 4. 전체 검색 결과 수
        long totalElements = indexDataRepository.count(searchSpec);

        // 5. 커서 조건 생성
        Specification<IndexData> cursorSpec =
                IndexDataSpecification.cursor(
                        sortField,
                        sortDirection,
                        request.getCursor(),
                        request.getIdAfter()
                );

        // 6. 검색 조건과 커서 조건 결합
        Specification<IndexData> spec =
                searchSpec.and(cursorSpec);

        // 7. 정렬 조건
        Sort.Direction direction = sortDirection.equals("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, sortField)
                .and(Sort.by(direction, "id"));

        // 8. size + 1개 조회
        Pageable pageable = PageRequest.of(0, size + 1, sort);

        List<IndexData> results = indexDataRepository
                .findAll(spec, pageable)
                .getContent();

        // 9. 다음 페이지 존재 여부
        boolean hasNext = results.size() > size;

        List<IndexData> pageContent = hasNext
                ? results.subList(0, size)
                : results;

        // 10. DTO 변환
        List<IndexDataDto> content = pageContent.stream()
                .map(IndexDataDto::from)
                .toList();

        // 11. 다음 커서 생성
        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext && !pageContent.isEmpty()) {

            IndexData last = pageContent.get(pageContent.size() - 1);

            nextIdAfter = last.getId();

            nextCursor = switch (sortField) {
                case "baseDate" -> last.getBaseDate().toString();
                case "marketPrice" -> last.getMarketPrice().toString();
                case "closingPrice" -> last.getClosingPrice().toString();
                case "highPrice" -> last.getHighPrice().toString();
                case "lowPrice" -> last.getLowPrice().toString();
                case "versus" -> last.getVersus().toString();
                case "fluctuationRate" -> last.getFluctuationRate().toString();
                case "tradingQuantity" -> last.getTradingQuantity().toString();
                case "tradingPrice" -> last.getTradingPrice().toString();
                case "marketTotalAmount" -> last.getMarketTotalAmount().toString();

                default -> throw new IllegalArgumentException(
                        "지원하지 않는 정렬 필드"
                );
            };
        }

        // 12. 응답 반환
        return new CursorPageResponseIndexDataDto(
                content,
                nextCursor,
                nextIdAfter,
                size,
                totalElements,
                hasNext
        );
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
    public ResponseEntity<?> toCSV(IndexDataSearchRequest request) {
        return null;
    }


}

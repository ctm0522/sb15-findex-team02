package org.findex.team2.sb15findexteam02.service.basic;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSearchRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.CursorPageResponseIndexDataDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexChartDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexDataDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexPerformanceDto;
import org.findex.team2.sb15findexteam02.dto.response.ChartDataPoint;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.nio.charset.StandardCharsets;
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
    @Transactional(readOnly = true)
    public IndexChartDto Chart(Long id, PeriodType periodType) {
        IndexInfo info = indexInfoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("지수 정보를 찾을 수 없음: " + id));
        List<IndexData> history = indexDataRepository.findByIndexInfoIdOrderByBaseDateAscIdAsc(id);
        List<IndexData> sampled = sample(history, periodType);
        List<ChartDataPoint> points = sampled.stream()
                .map(d -> new ChartDataPoint(d.getBaseDate(), d.getClosingPrice()))
                .toList();
        return new IndexChartDto(id, info.getIndexCategory().getCategoryName(),
                info.getIndexName(), periodType, points, movingAverage(points, 5), movingAverage(points, 20));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankedIndexPerformanceDto> rank(Long id, PeriodType periodType, int limit) {
        if (limit < 1) throw new IllegalArgumentException("limit은 1 이상이어야 합니다.");
        List<IndexInfo> infos = id == null
                ? indexInfoRepository.findAll()
                : List.of(indexInfoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("지수 정보를 찾을 수 없음: " + id)));
        List<IndexPerformanceDto> performances = infos.stream()
                .map(info -> performance(info, periodType))
                .filter(java.util.Objects::nonNull)
                .sorted(Comparator.comparing(IndexPerformanceDto::getFluctuationRate).reversed()
                        .thenComparing(IndexPerformanceDto::getIndexInfoId))
                .limit(limit)
                .toList();
        List<RankedIndexPerformanceDto> ranked = new ArrayList<>();
        for (int i = 0; i < performances.size(); i++) {
            ranked.add(new RankedIndexPerformanceDto(performances.get(i), i + 1));
        }
        return ranked;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndexPerformanceDto> favorite(PeriodType periodType) {
        return indexInfoRepository.findAll().stream()
                .filter(IndexInfo::isFavorite)
                .map(info -> performance(info, periodType))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> toCSV(IndexDataSearchRequest request) {

        String field = request.getSortField() == null
                ? "baseDate"
                : request.getSortField();

        String direction = request.getSortDirection() == null
                ? "desc"
                : request.getSortDirection().toLowerCase(Locale.ROOT);

        Set<String> allowedFields = Set.of(
                "baseDate", "marketPrice", "closingPrice",
                "highPrice", "lowPrice", "versus",
                "fluctuationRate", "tradingQuantity",
                "tradingPrice", "marketTotalAmount"
        );

        if (!allowedFields.contains(field)) {
            throw new IllegalArgumentException(
                    "지원하지 않는 정렬 필드: " + field
            );
        }

        if (!Set.of("asc", "desc").contains(direction)) {
            throw new IllegalArgumentException(
                    "지원하지 않는 정렬 방향: " + direction
            );
        }

        if (request.getStartDate() != null
                && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException(
                    "시작 날짜가 종료 날짜보다 늦습니다."
            );
        }

        Sort.Direction sortDirection = direction.equals("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort sort = Sort.by(sortDirection, field)
                .and(Sort.by(sortDirection, "id"));

        // 검색 조건에 맞는 전체 데이터 조회
        List<IndexData> data = indexDataRepository.findAll(
                IndexDataSpecification.search(request),
                sort
        );

        // CSV 생성
        StringBuilder csv = new StringBuilder("\uFEFF");

        csv.append(
                "id,indexInfoId,baseDate,sourceType,"
                        + "marketPrice,closingPrice,highPrice,lowPrice,"
                        + "versus,fluctuationRate,tradingQuantity,"
                        + "tradingPrice,marketTotalAmount\r\n"
        );

        for (IndexData d : data) {
            csv.append(d.getId()).append(',')
                    .append(d.getIndexInfo().getId()).append(',')
                    .append(d.getBaseDate()).append(',')
                    .append(d.getSourceType()).append(',')
                    .append(d.getMarketPrice()).append(',')
                    .append(d.getClosingPrice()).append(',')
                    .append(d.getHighPrice()).append(',')
                    .append(d.getLowPrice()).append(',')
                    .append(d.getVersus()).append(',')
                    .append(d.getFluctuationRate()).append(',')
                    .append(d.getTradingQuantity()).append(',')
                    .append(d.getTradingPrice()).append(',')
                    .append(d.getMarketTotalAmount())
                    .append("\r\n");
        }

        // CSV 문자열을 바이트 배열로 변환
        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        // 다운로드 리소스 생성
        InputStream inputStream = new ByteArrayInputStream(bytes);
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(
                        new MediaType("text", "csv", StandardCharsets.UTF_8)
                )
                .contentLength(bytes.length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("index-data.csv", StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(resource);
    }

    private List<IndexData> sample(List<IndexData> history, PeriodType period) {
        if (period == null) throw new IllegalArgumentException("periodType이 필요합니다.");
        if (period == PeriodType.DAILY) return history;
        Map<String, IndexData> last = new LinkedHashMap<>();
        for (IndexData data : history) {
            LocalDate date = data.getBaseDate();
            String key;
            if (period == PeriodType.WEEKLY) {
                WeekFields wf = WeekFields.ISO;
                key = date.get(wf.weekBasedYear()) + "-W" + date.get(wf.weekOfWeekBasedYear());
            } else if (period == PeriodType.MONTHLY) {
                key = date.getYear() + "-" + date.getMonthValue();
            } else throw new IllegalArgumentException("지원하지 않는 기간: " + period);
            last.put(key, data);
        }
        return new ArrayList<>(last.values());
    }

    private List<ChartDataPoint> movingAverage(List<ChartDataPoint> points, int window) {
        List<ChartDataPoint> result = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < points.size(); i++) {
            sum = sum.add(points.get(i).getValue());
            if (i >= window) sum = sum.subtract(points.get(i - window).getValue());
            if (i >= window - 1) {
                result.add(new ChartDataPoint(points.get(i).getDate(),
                        sum.divide(BigDecimal.valueOf(window), 6, RoundingMode.HALF_UP)));
            }
        }
        return result;
    }

    // 기간 성과: 최신 거래일 종가와 해당 기간 전(또는 그 이전 가장 가까운 거래일) 종가 비교.
    private IndexPerformanceDto performance(IndexInfo info, PeriodType period) {
        List<IndexData> history = indexDataRepository.findByIndexInfoIdOrderByBaseDateAscIdAsc(info.getId());
        if (history.isEmpty()) return null;
        IndexData latest = history.get(history.size() - 1);
        LocalDate target = switch (period) {
            case DAILY -> latest.getBaseDate().minusDays(1);
            case WEEKLY -> latest.getBaseDate().minusWeeks(1);
            case MONTHLY -> latest.getBaseDate().minusMonths(1);
        };
        IndexData previous = null;
        for (IndexData d : history) {
            if (!d.getBaseDate().isAfter(target)) previous = d;
            else break;
        }
        if (previous == null || previous.getClosingPrice().signum() == 0) return null;
        BigDecimal current = latest.getClosingPrice();
        BigDecimal before = previous.getClosingPrice();
        BigDecimal diff = current.subtract(before);
        BigDecimal rate = diff.multiply(BigDecimal.valueOf(100))
                .divide(before, 6, RoundingMode.HALF_UP);
        return new IndexPerformanceDto(info.getId(), info.getIndexCategory().getCategoryName(),
                info.getIndexName(), diff, rate, current, before);
    }
}

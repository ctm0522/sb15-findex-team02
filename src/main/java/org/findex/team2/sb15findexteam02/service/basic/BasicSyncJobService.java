package org.findex.team2.sb15findexteam02.service.basic;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.client.PublicDataClient;
import org.findex.team2.sb15findexteam02.dto.request.*;
import org.findex.team2.sb15findexteam02.dto.response.*;
import org.findex.team2.sb15findexteam02.mapper.SyncJobDtoMapper;
import org.findex.team2.sb15findexteam02.model.*;
import org.findex.team2.sb15findexteam02.repository.IndexDataRepository;
import org.findex.team2.sb15findexteam02.repository.IndexInfoRepository;
import org.findex.team2.sb15findexteam02.repository.SyncJobRepository;
import org.findex.team2.sb15findexteam02.service.IndexDataService;
import org.findex.team2.sb15findexteam02.service.IndexInfoService;
import org.findex.team2.sb15findexteam02.service.SyncJobService;
import org.findex.team2.sb15findexteam02.specification.SyncJobSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicSyncJobService implements SyncJobService {
    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;
    private final SyncJobRepository syncJobRepository;
    private final SyncJobDtoMapper syncJobDtoMapper;

    private final IndexDataService indexDataService;
    private final IndexInfoService indexInfoService;
    private final PublicDataClient publicDataClient;

    @Override
    @Transactional
    public SyncJobDto create(SyncJobCreateRequest request, String result) {
        SyncJob syncJob = new SyncJob(request.getStatus(), request.getWorker(), request.getJobType(),
                request.getTargetDate(),  request.getIndexInfo());
        SyncJob createSyncJob = syncJobRepository.save(syncJob);
        return syncJobDtoMapper.toDto(createSyncJob,result);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseSyncJobDto findAll(
            SyncjobSearchRequest request
    ) {
        // 1. 기본값
        int size = request.getSize() == null
                ? 10 : request.getSize();

        String sortField = request.getSortField() == null
                ? "jobTime" : request.getSortField();

        String sortDirection = request.getSortDirection() == null
                ? "desc"
                : request.getSortDirection().toLowerCase(Locale.ROOT);

        // 2. 유효성 검증
        if (size < 1 || size == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "올바르지 않은 페이지 크기입니다."
            );
        }

        if (!Set.of("jobTime", "targetDate").contains(sortField)) {
            throw new IllegalArgumentException(
                    "지원하지 않는 정렬 필드: " + sortField
            );
        }

        if (!Set.of("asc", "desc").contains(sortDirection)) {
            throw new IllegalArgumentException(
                    "지원하지 않는 정렬 방향: " + sortDirection
            );
        }

        if (request.getBaseDateFrom() != null
                && request.getBaseDateTo() != null
                && request.getBaseDateFrom().isAfter(request.getBaseDateTo())) {
            throw new IllegalArgumentException(
                    "대상 날짜 범위가 올바르지 않습니다."
            );
        }

        if (request.getJobTimeFrom() != null
                && request.getJobTimeTo() != null
                && request.getJobTimeFrom().isAfter(request.getJobTimeTo())) {
            throw new IllegalArgumentException(
                    "작업 일시 범위가 올바르지 않습니다."
            );
        }

        // 3. 검색 조건 및 전체 데이터 수
        Specification<SyncJob> searchSpec =
                SyncJobSpecification.search(request);

        long totalElements = syncJobRepository.count(searchSpec);

        // 4. 커서 조건
        Specification<SyncJob> cursorSpec =
                SyncJobSpecification.cursor(
                        sortField,
                        sortDirection,
                        request.getCursor(),
                        request.getIdAfter()
                );

        // 5. 정렬 조건
        boolean ascending = sortDirection.equals("asc");

        Specification<SyncJob> orderSpec = (root, query, cb) -> {

            List<jakarta.persistence.criteria.Order> orders =
                    new ArrayList<>();

            if (sortField.equals("targetDate")) {
                // 날짜가 null이면 1, 아니면 0
                // 따라서 null 데이터는 항상 마지막에 정렬
                orders.add(cb.asc(
                        cb.selectCase()
                                .when(cb.isNull(root.get("targetDate")), 1)
                                .otherwise(0)
                ));
            }

            orders.add(ascending
                    ? cb.asc(root.get(sortField))
                    : cb.desc(root.get(sortField)));

            orders.add(ascending
                    ? cb.asc(root.get("id"))
                    : cb.desc(root.get("id")));

            query.orderBy(orders);

            return cb.conjunction();
        };

        Specification<SyncJob> spec = searchSpec
                .and(cursorSpec)
                .and(orderSpec);

        // 6. size + 1개 조회
        Pageable pageable = PageRequest.of(0, size + 1);

        List<SyncJob> results = syncJobRepository
                .findAll(spec, pageable)
                .getContent();

        // 7. 다음 페이지 여부
        boolean hasNext = results.size() > size;

        List<SyncJob> pageContent = hasNext
                ? results.subList(0, size)
                : results;

        // 8. DTO 변환
        List<SyncJobDto> content = pageContent.stream()
                .map(SyncJobDto::from)
                .toList();

        // 9. 다음 커서 생성
        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext && !pageContent.isEmpty()) {
            SyncJob last = pageContent.get(pageContent.size() - 1);

            nextIdAfter = last.getId();

            nextCursor = switch (sortField) {
                case "jobTime" -> last.getJobTime().toString();
                case "targetDate" -> last.getTargetDate() == null
                        ? null
                        : last.getTargetDate().toString();
                default -> throw new IllegalArgumentException(
                        "지원하지 않는 정렬 필드"
                );
            };
        }

        // 10. 응답 반환
        return new CursorPageResponseSyncJobDto(
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
    public List<SyncJobDto> convertToIndexDataClientDtoDto(IndexDataSyncRequest request, String worker) {
        List<Long> ids = request.getIndexInfoIds();
        List<String> infoNames = new ArrayList<>();
        List<String> categoryNames = new ArrayList<>();

        ids.forEach(id -> {
            IndexInfo indexInfo = indexInfoRepository.findById(id).
                    orElseThrow(() -> new NoSuchElementException("지수 정보 찾을 수 없음"+id));
            infoNames.add(indexInfo.getIndexName());
            categoryNames.add(indexInfo.getIndexCategory().getCategoryName());
        });

         IndexDataClientDto indexDataClientDto = new IndexDataClientDto(infoNames,categoryNames,request.getBaseDateFrom(),request.getBaseDateTo(),worker);
         publicDataClient.fetchIndexData(indexDataClientDto);
         return null;
    }

    @Override
    @Transactional
    public List<SyncJobDto> syncInfo(List<PublicDataInfoResponse> requests, String worker, ResultType status) {
        String result;
        IndexInfo indexInfo;
        SyncJobDto syncJobDto;
        List<SyncJobDto> dtos = new ArrayList<>();
        for(PublicDataInfoResponse entry : requests){
            if(indexInfoRepository.existsByIndexNameAndIndexCategoryCategoryName(entry.getIndexName(),entry.getIndexClassification())){
                indexInfo = indexInfoRepository.findByIndexNameAndIndexCategoryCategoryName(entry.getIndexName(),entry.getIndexClassification()).get();
                indexInfo.update(entry.getEmployedItemCount(),entry.getBasDate(),entry.getBaseIndex(),indexInfo.isFavorite(),SourceType.OPEN_API);
                result = "UPDATE";
            } else {
                IndexInfoCreateRequest indexInfoCreateRequest = new IndexInfoCreateRequest(entry.getIndexClassification(),entry.getIndexName(),
                        entry.getEmployedItemCount(),entry.getBasDate(),entry.getBaseIndex(),false);
                IndexInfoDto indexInfoDto = indexInfoService.create(indexInfoCreateRequest, SourceType.OPEN_API);
                indexInfo = indexInfoRepository.findById(indexInfoDto.getId()).
                        orElseThrow(() -> new NoSuchElementException("지수 찾을 수 없음 : " + indexInfoDto.getId()));
                result = "NEW";
            }
            SyncJobCreateRequest syncJobCreateRequest = new SyncJobCreateRequest(status,worker, JobType.INDEX_INFO, null, indexInfo);
            syncJobDto = create(syncJobCreateRequest,result);
            dtos.add(syncJobDto);

        }
        //여기서 할일
        //1.exist로 있나 없나 확인
        //없으면 create 있으면 update 호출
        //싱크잡크리에이트 호출

        return dtos;
    }

    @Override
    @Transactional
    public List<SyncJobDto> syncData(List<PublicDataResponse> requests, String worker, ResultType status) {
        String result;
        IndexInfo indexInfo;
        IndexData indexData;
        SyncJobDto syncJobDto;
        List<SyncJobDto> dtos = new ArrayList<>();


        for(PublicDataResponse entry : requests){
            indexInfo = indexInfoRepository.findByIndexNameAndIndexCategoryCategoryName(entry.getInfoNames(),entry.getCategoryNames()).orElseThrow(
                    () -> new NoSuchElementException("지수정보 없음" + entry.getInfoNames() + ", " + entry.getCategoryNames()));

            if(indexDataRepository.existsByBaseDateAndIndexInfoId(entry.getBaseDate(),indexInfo.getId())){
                indexData = indexDataRepository.findByBaseDateAndIndexInfoId(entry.getBaseDate(),indexInfo.getId());
                indexData.update(entry.getMarketPrice(),entry.getDailyPrice(),entry.getHighPrice(),entry.getLowPrice(),
                        entry.getVs(),entry.getFluctuationRate(),entry.getTradingQuantity(),entry.getTradingPrice(),entry.getMarketTotalAmount(),SourceType.OPEN_API);

                result = "UPDATE";

            }
            else {
                IndexDataCreateRequest indexDataCreateRequest = new IndexDataCreateRequest(indexInfo.getId(),entry.getBaseDate(),entry.getMarketPrice(),
                        entry.getDailyPrice(),entry.getHighPrice(),entry.getLowPrice(),entry.getVs(),entry.getFluctuationRate(),
                        entry.getTradingQuantity(),entry.getTradingPrice(),entry.getMarketTotalAmount());

                indexDataService.create(indexDataCreateRequest, SourceType.OPEN_API);
                result = "NEW";
            }

            SyncJobCreateRequest syncJobCreateRequest = new SyncJobCreateRequest(status,worker, JobType.INDEX_DATA, entry.getBaseDate(), indexInfo);
            syncJobDto = create(syncJobCreateRequest,result);
            dtos.add(syncJobDto);

        }



        return dtos;
    }
}

package org.findex.team2.sb15findexteam02.service.basic;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoSearchRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.CursorPageResponseIndexInfoDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexInfoDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexInfoSummaryDto;
import org.findex.team2.sb15findexteam02.model.AutoSyncConfig;
import org.findex.team2.sb15findexteam02.model.IndexCategory;
import org.findex.team2.sb15findexteam02.model.IndexInfo;
import org.findex.team2.sb15findexteam02.model.SourceType;
import org.findex.team2.sb15findexteam02.repository.AutoSyncConfigRepository;
import org.findex.team2.sb15findexteam02.repository.IndexCategoryRepository;
import org.findex.team2.sb15findexteam02.repository.IndexInfoRepository;
import org.findex.team2.sb15findexteam02.service.IndexInfoService;
import org.findex.team2.sb15findexteam02.specification.IndexInfoSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicIndexInfoService implements IndexInfoService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexCategoryRepository indexCategoryRepository;
    private final AutoSyncConfigRepository autoSyncConfigRepository;

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseIndexInfoDto findAll(
            IndexInfoSearchRequest request
    ) {

        // 1. 기본값 설정
        int size = request.getSize() == null
                ? 10
                : request.getSize();

        String sortField = request.getSortField() == null
                ? "indexClassification"
                : request.getSortField();

        String sortDirection = request.getSortDirection() == null
                ? "asc"
                : request.getSortDirection().toLowerCase(Locale.ROOT);

        // 2. 요청값 검증
        if (size < 1) {
            throw new IllegalArgumentException(
                    "size는 1 이상이어야 합니다."
            );
        }

        Set<String> allowedFields = Set.of(
                "indexClassification",
                "indexName",
                "employedItemsCount"
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

        String cursor = request.getCursor();
        Long idAfter = request.getIdAfter();
        if ((cursor == null) != (idAfter == null)) {
            throw new IllegalArgumentException("cursor와 idAfter는 함께 전달해야 합니다.");
        }
        if (cursor != null) {
            if (cursor.isBlank()) {
                throw new IllegalArgumentException("cursor는 빈 문자열일 수 없습니다.");
            }
            if ("employedItemsCount".equals(sortField)) {
                try {
                    Integer.parseInt(cursor);
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException(
                            "employedItemsCount 정렬의 cursor는 정수여야 합니다.", exception);
                }
            }
        }

        // 3. 검색 조건 생성
        Specification<IndexInfo> searchSpec =
                IndexInfoSpecification.search(request);

        // 4. 전체 검색 결과 개수
        long totalElements = indexInfoRepository.count(searchSpec);

        // 5. 커서 조건 생성
        Specification<IndexInfo> cursorSpec =
                IndexInfoSpecification.cursor(
                        sortField,
                        sortDirection,
                        request.getCursor(),
                        request.getIdAfter()
                );

        // 6. 검색 조건 + 커서 조건 결합
        Specification<IndexInfo> spec =
                searchSpec.and(cursorSpec);

        // 7. 정렬 조건 생성
        Sort.Direction direction = sortDirection.equals("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        String sortProperty = sortField.equals("indexClassification")
                ? "indexCategory.categoryName"
                : sortField;

        Sort sort = Sort.by(direction, sortProperty)
                .and(Sort.by(direction, "id"));

        // 8. size + 1개 조회
        Pageable pageable = PageRequest.of(0, size + 1, sort);

        List<IndexInfo> results = indexInfoRepository
                .findAll(spec, pageable)
                .getContent();

        // 9. 다음 페이지 존재 여부
        boolean hasNext = results.size() > size;

        List<IndexInfo> pageContent = hasNext
                ? results.subList(0, size)
                : results;

        // 10. DTO 변환
        List<IndexInfoSummaryDto> content = pageContent.stream()
                .map(IndexInfoSummaryDto::from)
                .toList();

        // 11. 다음 커서 생성
        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext && !pageContent.isEmpty()) {

            IndexInfo last = pageContent.get(pageContent.size() - 1);

            nextIdAfter = last.getId();

            nextCursor = switch (sortField) {
                case "indexClassification" ->
                        last.getIndexCategory().getCategoryName();

                case "indexName" ->
                        last.getIndexName();

                case "employedItemsCount" ->
                        String.valueOf(last.getEmployedItemsCount());

                default -> throw new IllegalArgumentException(
                        "지원하지 않는 정렬 필드"
                );
            };
        }

        // 12. 응답 반환
        return new CursorPageResponseIndexInfoDto(
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
    public IndexInfoDto create(IndexInfoCreateRequest request, SourceType sourceType) {
        IndexCategory category;
        if(indexCategoryRepository.existsByCategoryName(request.getIndexClassification()))
        {
            category = indexCategoryRepository.findByCategoryName(request.getIndexClassification());
        }
        else {
            category = new IndexCategory(request.getIndexClassification());
            indexCategoryRepository.save(category);
        }

        IndexInfo indexInfo = new IndexInfo(category,request.getIndexName(),request.getEmployedItemsCount(),
                request.getBasePointInTime(),request.getBaseIndex(),sourceType,request.getFavorite());
        IndexInfo createIndexInfo = indexInfoRepository.save(indexInfo);

        AutoSyncConfig autoSyncConfig = new AutoSyncConfig(createIndexInfo);
        autoSyncConfigRepository.save(autoSyncConfig);


        return IndexInfoDto.from(createIndexInfo);
    }// todo 나중에 오토싱크 생성 추가 삭제는 ddl cascade가 할 것

    @Override
    @Transactional(readOnly = true)
    public IndexInfoDto find(Long id) {
        IndexInfo indexInfo = indexInfoRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 id 지수없음 : "+id));
        return IndexInfoDto.from(indexInfo);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        indexInfoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public IndexInfoDto update(Long id, IndexInfoUpdateRequest request, SourceType sourceType) {
        IndexInfo indexInfo = indexInfoRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 id 지수없음 : "+id));
        indexInfo.update(request.getEmployedItemsCount(),request.getBasePointInTime(),request.getBaseIndex(),request.getFavorite(),sourceType);
        return IndexInfoDto.from(indexInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndexInfoSummaryDto> summaries() {
        List<IndexInfo> indexInfos = indexInfoRepository.findAll();
        List<IndexInfoSummaryDto> summaryDtos = new ArrayList<>();

        for(IndexInfo indexInfo : indexInfos){
            summaryDtos.add(IndexInfoSummaryDto.from(indexInfo));
        }

        return summaryDtos;
    }
}

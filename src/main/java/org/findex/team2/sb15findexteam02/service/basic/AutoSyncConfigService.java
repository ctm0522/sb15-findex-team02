package org.findex.team2.sb15findexteam02.service.basic;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.AutoSyncConfigSearchRequest;
import org.findex.team2.sb15findexteam02.dto.request.AutoSyncConfigUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.AutoSyncConfigDto;
import org.findex.team2.sb15findexteam02.dto.response.CursorPageResponseAutoSyncConfigDto;
import org.findex.team2.sb15findexteam02.model.AutoSyncConfig;
import org.findex.team2.sb15findexteam02.repository.AutoSyncConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutoSyncConfigService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("indexInfo.indexName", "enabled");

    private static final Set<String> ALLOWED_SORT_DIRECTIONS =
            Set.of("asc", "desc");

    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final ObjectMapper objectMapper;

    public CursorPageResponseAutoSyncConfigDto findAll(
            AutoSyncConfigSearchRequest request
    ) {
        String sortField = request.getSortField() == null
                ? "indexInfo.indexName"
                : request.getSortField();

        String sortDirection = request.getSortDirection() == null
                ? "asc"
                : request.getSortDirection().toLowerCase();

        Integer requestedSize = request.getSize();
        int size = requestedSize == null ? 10 : requestedSize;

        validateSearchRequest(sortField, sortDirection, size, request);

        CursorPayload cursorPayload = decodeCursor(request.getCursor());

        if (cursorPayload != null) {
            if (!sortField.equals(cursorPayload.sortField())
                    || !sortDirection.equals(cursorPayload.sortDirection())) {
                throw new IllegalArgumentException(
                        "커서의 정렬 조건과 요청의 정렬 조건이 일치하지 않습니다."
                );
            }
        }

        String cursorValue = cursorPayload == null
                ? null
                : cursorPayload.sortValue();

        Long cursorId = cursorPayload == null
                ? null
                : cursorPayload.id();

        List<AutoSyncConfig> results =
                autoSyncConfigRepository.searchCursor(
                        request.getIndexInfoId(),
                        request.getEnabled(),
                        sortField,
                        sortDirection,
                        cursorValue,
                        cursorId,
                        size + 1
                );

        boolean hasNext = results.size() > size;

        List<AutoSyncConfig> pageItems = hasNext
                ? results.subList(0, size)
                : results;

        List<AutoSyncConfigDto> content = pageItems.stream()
                .map(this::toDto)
                .toList();

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext && !pageItems.isEmpty()) {
            AutoSyncConfig lastItem =
                    pageItems.get(pageItems.size() - 1);

            String sortValue = getSortValue(lastItem, sortField);

            nextCursor = encodeCursor(
                    new CursorPayload(
                            sortField,
                            sortDirection,
                            sortValue,
                            lastItem.getId()
                    )
            );

            nextIdAfter = lastItem.getId();
        }

        long totalElements = autoSyncConfigRepository.countByFilters(
                request.getIndexInfoId(),
                request.getEnabled()
        );

        return new CursorPageResponseAutoSyncConfigDto(
                content,
                nextCursor,
                nextIdAfter,
                size,
                totalElements,
                hasNext
        );
    }

    @Transactional
    public AutoSyncConfigDto update(
            Long id,
            AutoSyncConfigUpdateRequest request
    ) {
        if (request.getEnabled() == null) {
            throw new IllegalArgumentException(
                    "enabled 값은 필수입니다."
            );
        }

        int updatedRows = autoSyncConfigRepository.updateEnabled(
                id,
                request.getEnabled()
        );

        if (updatedRows == 0) {
            throw new IllegalStateException(
                    "AutoSyncConfig을 찾을 수 없습니다. id=" + id
            );
        }

        AutoSyncConfig updated = autoSyncConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "AutoSyncConfig을 찾을 수 없습니다. id=" + id
                ));

        return toDto(updated);
    }

    private void validateSearchRequest(
            String sortField,
            String sortDirection,
            int size,
            AutoSyncConfigSearchRequest request
    ) {
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException(
                    "지원하지 않는 sortField입니다."
            );
        }

        if (!ALLOWED_SORT_DIRECTIONS.contains(sortDirection)) {
            throw new IllegalArgumentException(
                    "sortDirection은 asc 또는 desc여야 합니다."
            );
        }

        if (size < 1 || size == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "size는 1 이상의 유효한 정수여야 합니다."
            );
        }

        if (request.getIdAfter() != null
                && (request.getCursor() == null
                || request.getCursor().isBlank())) {
            throw new IllegalArgumentException(
                    "idAfter만으로는 조회할 수 없습니다. cursor를 전달해주세요."
            );
        }
    }

    private CursorPayload decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(cursor);
            String json = new String(
                    decodedBytes,
                    StandardCharsets.UTF_8
            );

            CursorPayload payload =
                    objectMapper.readValue(json, CursorPayload.class);

            if (payload.sortField() == null
                    || payload.sortDirection() == null
                    || payload.sortValue() == null
                    || payload.id() == null
                    || payload.id() < 0) {
                throw new IllegalArgumentException(
                        "커서 데이터가 올바르지 않습니다."
                );
            }

            if (!ALLOWED_SORT_FIELDS.contains(payload.sortField())
                    || !ALLOWED_SORT_DIRECTIONS.contains(
                    payload.sortDirection()
            )) {
                throw new IllegalArgumentException(
                        "커서의 정렬 조건이 올바르지 않습니다."
                );
            }

            if ("enabled".equals(payload.sortField())
                    && !"true".equals(payload.sortValue())
                    && !"false".equals(payload.sortValue())) {
                throw new IllegalArgumentException(
                        "커서의 enabled 값이 올바르지 않습니다."
                );
            }

            return payload;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "cursor 값이 올바르지 않습니다.", e
            );
        } catch (JacksonException e) {
            throw new IllegalArgumentException(
                    "cursor 데이터 형식이 올바르지 않습니다.", e
            );
        }
    }

    private String encodeCursor(CursorPayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(
                            json.getBytes(StandardCharsets.UTF_8)
                    );
        } catch (JacksonException e) {
            throw new IllegalStateException(
                    "cursor 생성에 실패했습니다.", e
            );
        }
    }

    private String getSortValue(
            AutoSyncConfig config,
            String sortField
    ) {
        if ("enabled".equals(sortField)) {
            return String.valueOf(config.isEnabled());
        }

        return config.getIndexInfo().getIndexName();
    }

    private AutoSyncConfigDto toDto(AutoSyncConfig config) {
        return new AutoSyncConfigDto(
                config.getId(),
                config.getIndexInfo().getId(),
                config.getIndexInfo()
                        .getIndexCategory()
                        .getCategoryName(),
                config.getIndexInfo().getIndexName(),
                config.isEnabled()
        );
    }

    private record CursorPayload(
            String sortField,
            String sortDirection,
            String sortValue,
            Long id
    ) {
    }
}

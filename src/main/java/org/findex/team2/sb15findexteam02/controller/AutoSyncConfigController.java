package org.findex.team2.sb15findexteam02.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.findex.team2.sb15findexteam02.dto.response.*;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.AutoSyncConfigSearchRequest;
import org.findex.team2.sb15findexteam02.dto.request.AutoSyncConfigUpdateRequest;
import org.findex.team2.sb15findexteam02.service.basic.AutoSyncConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "자동 연동 설정 API", description = "자동 연동 설정 관리 API")
@RestController
@RequestMapping("/api/auto-sync-configs")
@RequiredArgsConstructor
public class AutoSyncConfigController {

    private final AutoSyncConfigService autoSyncConfigService;

    @Operation(
        summary = "자동 연동 설정 수정",
        description = "기존 자동 연동 설정을 수정합니다.",
        parameters = {
            @Parameter(name = "id", description = "자동 연동 설정 ID", in = ParameterIn.PATH, required = true, schema = @Schema(type = "integer", format = "int64"))
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoSyncConfigUpdateRequest.class))
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "자동 연동 설정 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutoSyncConfigDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 설정 값 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "수정할 자동 연동 설정을 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "데이터 제약 조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AutoSyncConfigDto> update(
            @PathVariable Long id,
            @RequestBody AutoSyncConfigUpdateRequest request
    ) {
        return ResponseEntity.ok(
                autoSyncConfigService.update(id, request)
        );
    }

    @Operation(
        summary = "자동 연동 설정 목록 조회",
        description = "자동 연동 설정 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.",
        parameters = {
            @Parameter(name = "indexInfoId", description = "지수 정보 ID", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "enabled", description = "활성화 여부", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "boolean")),
            @Parameter(name = "idAfter", description = "이전 페이지 마지막 요소 ID", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "cursor", description = "커서 (다음 페이지 시작점)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string")),
            @Parameter(name = "sortField", description = "정렬 필드 (indexInfo.indexName, enabled)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "indexInfo.indexName")),
            @Parameter(name = "sortDirection", description = "정렬 방향 (asc, desc)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "asc")),
            @Parameter(name = "size", description = "페이지 크기", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int32", defaultValue = "10"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "자동 연동 설정 목록 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CursorPageResponseAutoSyncConfigDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<CursorPageResponseAutoSyncConfigDto> findAll(
            @Parameter(hidden = true) @ModelAttribute AutoSyncConfigSearchRequest request
    ) {
        return ResponseEntity.ok(
                autoSyncConfigService.findAll(request)
        );
    }
}

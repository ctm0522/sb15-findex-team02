package org.findex.team2.sb15findexteam02.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.findex.team2.sb15findexteam02.dto.response.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.client.PublicDataClient;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSyncRequest;
import org.findex.team2.sb15findexteam02.dto.request.SyncjobSearchRequest;
import org.findex.team2.sb15findexteam02.service.SyncJobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "연동 작업 API", description = "연동 작업 관리 API")
@RestController
@RequestMapping("/api/sync-jobs")
@RequiredArgsConstructor
public class SyncJobController {

    private final SyncJobService syncJobService;
    private final PublicDataClient publicDataClient;

    @Operation(
        summary = "지수 정보 연동",
        description = "Open API를 통해 지수 정보를 연동합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "연동 작업 생성 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SyncJobDto.class)))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "데이터 제약 조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "502", description = "외부 API 호출 또는 응답 처리 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/index-infos")
    public ResponseEntity<List<SyncJobDto>> syncIndexInfos(HttpServletRequest request) {
        //워커관련
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(publicDataClient.fetchIndexInfo(request.getRemoteAddr()));
    }

    @Operation(
        summary = "지수 데이터 연동",
        description = "Open API를 통해 지수 데이터를 연동합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexDataSyncRequest.class))
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "연동 작업 생성 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SyncJobDto.class)))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 날짜 범위 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "지수 정보를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "데이터 제약 조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "502", description = "외부 API 호출 또는 응답 처리 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/index-data")
    public ResponseEntity<List<SyncJobDto>>syncIndexData(
            @RequestBody IndexDataSyncRequest request,
            HttpServletRequest httpServletRequest
    ){
        String worker = httpServletRequest.getRemoteAddr();

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(syncJobService.convertToIndexDataClientDtoDto(request, worker));
    }

    @Operation(
        summary = "연동 작업 목록 조회",
        description = "연동 작업 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.",
        parameters = {
            @Parameter(name = "jobType", description = "연동 작업 유형 (INDEX_INFO, INDEX_DATA)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", allowableValues = {"INDEX_INFO", "INDEX_DATA"})),
            @Parameter(name = "indexInfoId", description = "지수 정보 ID", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "baseDateFrom", description = "대상 날짜 (부터)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", format = "date")),
            @Parameter(name = "baseDateTo", description = "대상 날짜 (까지)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", format = "date")),
            @Parameter(name = "worker", description = "작업자", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string")),
            @Parameter(name = "jobTimeFrom", description = "작업 일시 (부터)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", format = "date-time")),
            @Parameter(name = "jobTimeTo", description = "작업 일시 (까지)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", format = "date-time")),
            @Parameter(name = "status", description = "작업 상태 (SUCCESS, FAILED)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", allowableValues = {"SUCCESS", "FAILED"})),
            @Parameter(name = "idAfter", description = "이전 페이지 마지막 요소 ID", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "cursor", description = "커서 (다음 페이지 시작점)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string")),
            @Parameter(name = "sortField", description = "정렬 필드 (targetDate, jobTime)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "jobTime")),
            @Parameter(name = "sortDirection", description = "정렬 방향 (asc, desc)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "desc")),
            @Parameter(name = "size", description = "페이지 크기", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int32", defaultValue = "10"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "연동 작업 목록 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CursorPageResponseSyncJobDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<CursorPageResponseSyncJobDto> findAll(
            @Parameter(hidden = true) @ModelAttribute SyncjobSearchRequest request
    ) {
        return ResponseEntity.ok(
                syncJobService.findAll(request)
        );
    }


}

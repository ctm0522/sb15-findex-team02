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

import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSearchRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.*;
import org.findex.team2.sb15findexteam02.model.PeriodType;
import org.findex.team2.sb15findexteam02.model.ChartPeriodType;
import org.findex.team2.sb15findexteam02.model.SourceType;
import org.findex.team2.sb15findexteam02.service.IndexDataService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "지수 데이터 API", description = "지수 데이터 관리 API")
@RestController
@AllArgsConstructor
@RequestMapping("/api/index-data")
public class IndexDataController {

    private final IndexDataService indexDataService;

    @Operation(
        summary = "지수 데이터 목록 조회",
        description = "지수 데이터 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.",
        parameters = {
            @Parameter(name = "indexInfoId", description = "지수 정보 ID", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "startDate", description = "시작 일자", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", format = "date")),
            @Parameter(name = "endDate", description = "종료 일자", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", format = "date")),
            @Parameter(name = "idAfter", description = "이전 페이지 마지막 요소 ID", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "cursor", description = "커서 (다음 페이지 시작점)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string")),
            @Parameter(name = "sortField", description = "정렬 필드 (baseDate, marketPrice, closingPrice, highPrice, lowPrice, versus, fluctuationRate, tradingQuantity, tradingPrice, marketTotalAmount)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "baseDate")),
            @Parameter(name = "sortDirection", description = "정렬 방향 (asc, desc)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "desc")),
            @Parameter(name = "size", description = "페이지 크기", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int32", defaultValue = "10"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "지수 데이터 목록 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CursorPageResponseIndexDataDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<CursorPageResponseIndexDataDto> findAll(
            @Parameter(hidden = true) @ModelAttribute IndexDataSearchRequest request
            ) {
        return ResponseEntity.ok(indexDataService.findAll(request));
    }

    @Operation(
        summary = "지수 데이터 등록",
        description = "새로운 지수 데이터를 등록합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexDataCreateRequest.class))
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "지수 데이터 생성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexDataDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 데이터 값 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "참조하는 지수 정보를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "데이터 제약 조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<IndexDataDto> create(
            @RequestBody IndexDataCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(indexDataService.create(request, SourceType.USER));
    }

    @Operation(
        summary = "지수 데이터 삭제",
        description = "지수 데이터를 삭제합니다.",
        parameters = {
            @Parameter(name = "id", description = "지수 데이터 ID", in = ParameterIn.PATH, required = true, schema = @Schema(type = "integer", format = "int64"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "지수 데이터 삭제 성공", content = @Content),
        @ApiResponse(responseCode = "404", description = "삭제할 지수 데이터를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "데이터 제약 조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        indexDataService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "지수 데이터 수정",
        description = "기존 지수 데이터를 수정합니다.",
        parameters = {
            @Parameter(name = "id", description = "지수 데이터 ID", in = ParameterIn.PATH, required = true, schema = @Schema(type = "integer", format = "int64"))
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexDataUpdateRequest.class))
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "지수 데이터 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexDataDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 데이터 값 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "수정할 지수 데이터를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "데이터 제약 조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<IndexDataDto> patch(
            @PathVariable Long id,
            @RequestBody IndexDataUpdateRequest request
    ) {
        return ResponseEntity.ok(indexDataService.update(id, request, SourceType.USER));
    }

    @Operation(
        summary = "지수 차트 조회",
        description = "지수의 차트 데이터를 조회합니다. 최근 1·3·12개월의 일별 종가와 5·20거래일 이동평균을 최신순으로 반환합니다.",
        parameters = {
            @Parameter(name = "id", description = "지수 정보 ID", in = ParameterIn.PATH, required = true, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "periodType", description = "차트 조회 범위: MONTHLY(최근 1개월), QUARTERLY(최근 3개월), YEARLY(최근 12개월)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "MONTHLY", allowableValues = {"MONTHLY", "QUARTERLY", "YEARLY"}))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "차트 데이터 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexChartDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 기간 유형 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "지수 정보를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/chart")
    public ResponseEntity<IndexChartDto> Chart(
            @PathVariable Long id,
            @RequestParam(defaultValue = "MONTHLY") ChartPeriodType periodType
    ) {
        return ResponseEntity.ok(indexDataService.Chart(id, periodType));
    }

    @Operation(
        summary = "지수 성과 랭킹 조회",
        description = "지수의 성과 분석 랭킹을 조회합니다.",
        parameters = {
            @Parameter(name = "indexInfoId", description = "지수 정보 ID", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "periodType", description = "성과 기간 유형 (DAILY, WEEKLY, MONTHLY)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "DAILY", allowableValues = {"DAILY", "WEEKLY", "MONTHLY"})),
            @Parameter(name = "limit", description = "최대 랭킹 수", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int32", defaultValue = "10"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성과 랭킹 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RankedIndexPerformanceDto.class)))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 기간 유형 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/performance/rank")
    public ResponseEntity<List<RankedIndexPerformanceDto>> rank(
            @RequestParam(required = false) Long indexInfoId,
            @RequestParam(defaultValue = "DAILY") PeriodType periodType,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                indexDataService.rank(indexInfoId, periodType, limit)
        );
    }

    @Operation(
        summary = "관심 지수 성과 조회",
        description = "즐겨찾기로 등록된 지수들의 성과를 조회합니다.",
        parameters = {
            @Parameter(name = "periodType", description = "성과 기간 유형 (DAILY, WEEKLY, MONTHLY)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "DAILY", allowableValues = {"DAILY", "WEEKLY", "MONTHLY"}))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "관심 지수 성과 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = IndexPerformanceDto.class)))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/performance/favorite")
    public ResponseEntity<List<IndexPerformanceDto>> favorite(
            @RequestParam(defaultValue = "DAILY") PeriodType periodType
    ) {
        return ResponseEntity.ok(
                indexDataService.favorite(periodType)
        );
    }

    @Operation(
        summary = "지수 데이터 CSV export",
        description = "지수 데이터를 CSV 파일로 export합니다. 커서와 페이지 크기 없이 검색 조건에 맞는 전체 데이터를 다운로드합니다.",
        parameters = {
            @Parameter(name = "indexInfoId", description = "지수 정보 ID", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "startDate", description = "시작 일자", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", format = "date")),
            @Parameter(name = "endDate", description = "종료 일자", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", format = "date")),
            @Parameter(name = "sortField", description = "정렬 필드 (baseDate, marketPrice, closingPrice, highPrice, lowPrice, versus, fluctuationRate, tradingQuantity, tradingPrice, marketTotalAmount)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "baseDate")),
            @Parameter(name = "sortDirection", description = "정렬 방향 (asc, desc)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "desc"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CSV 파일 생성 성공", content = @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/export/csv")
    public ResponseEntity<?> toCSV(
            @Parameter(hidden = true) @ModelAttribute IndexDataSearchRequest request
    ) {
        byte[] csv = indexDataService.toCSV(request);

        InputStream inputStream = new ByteArrayInputStream(csv);
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .contentLength(csv.length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("index-data.csv", StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(resource);
    }
}

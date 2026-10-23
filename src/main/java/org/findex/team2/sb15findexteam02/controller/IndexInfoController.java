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

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoSearchRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoUpdateRequest;
import org.findex.team2.sb15findexteam02.model.SourceType;
import org.findex.team2.sb15findexteam02.service.IndexInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "지수 정보 API", description = "지수 정보 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-infos")
public class IndexInfoController {

    private final IndexInfoService indexInfoService;

    @Operation(
        summary = "지수 정보 목록 조회",
        description = "지수 정보 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.",
        parameters = {
            @Parameter(name = "indexClassification", description = "지수 분류명", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string")),
            @Parameter(name = "indexName", description = "지수명", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string")),
            @Parameter(name = "favorite", description = "즐겨찾기 여부", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "boolean")),
            @Parameter(name = "idAfter", description = "이전 페이지 마지막 요소 ID", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int64")),
            @Parameter(name = "cursor", description = "커서 (다음 페이지 시작점)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string")),
            @Parameter(name = "sortField", description = "정렬 필드 (indexClassification, indexName, employedItemsCount)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "indexClassification")),
            @Parameter(name = "sortDirection", description = "정렬 방향 (asc, desc)", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "string", defaultValue = "asc")),
            @Parameter(name = "size", description = "페이지 크기", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "integer", format = "int32", defaultValue = "10"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "지수 정보 목록 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CursorPageResponseIndexInfoDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<CursorPageResponseIndexInfoDto> findAll(
            @Parameter(hidden = true) @ModelAttribute IndexInfoSearchRequest request
    ) {
        return ResponseEntity.ok(
                indexInfoService.findAll(request)
        );
    }

    @Operation(
        summary = "지수 정보 등록",
        description = "새로운 지수 정보를 등록합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexInfoCreateRequest.class))
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "지수 정보 생성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexInfoDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (필수 필드 누락 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "데이터 제약 조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<IndexInfoDto> create(
            @RequestBody IndexInfoCreateRequest request
    ) {
        return ResponseEntity.ok(
                indexInfoService.create(request, SourceType.USER)
        );
    }

    @Operation(
        summary = "지수 정보 조회",
        description = "ID로 지수 정보를 조회합니다.",
        parameters = {
            @Parameter(name = "id", description = "지수 정보 ID", in = ParameterIn.PATH, required = true, schema = @Schema(type = "integer", format = "int64"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "지수 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexInfoDto.class))),
        @ApiResponse(responseCode = "404", description = "조회할 지수 정보를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<IndexInfoDto> find(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                indexInfoService.find(id)
        );
    }

    @Operation(
        summary = "지수 정보 삭제",
        description = "지수 정보를 삭제합니다. 관련된 지수 데이터도 함께 삭제됩니다.",
        parameters = {
            @Parameter(name = "id", description = "지수 정보 ID", in = ParameterIn.PATH, required = true, schema = @Schema(type = "integer", format = "int64"))
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "지수 정보 삭제 성공", content = @Content),
        @ApiResponse(responseCode = "404", description = "삭제할 지수 정보를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "데이터 제약 조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        indexInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "지수 정보 수정",
        description = "기존 지수 정보를 수정합니다. 즐겨찾기만 변경하면 기존 소스 타입을 유지합니다.",
        parameters = {
            @Parameter(name = "id", description = "지수 정보 ID", in = ParameterIn.PATH, required = true, schema = @Schema(type = "integer", format = "int64"))
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexInfoUpdateRequest.class))
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "지수 정보 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexInfoDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필드 값 등)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "수정할 지수 정보를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "데이터 제약 조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<IndexInfoDto> patch(
            @PathVariable Long id,
            @RequestBody IndexInfoUpdateRequest request
    ) {
        return ResponseEntity.ok(
                indexInfoService.update(id, request, SourceType.USER)
        );
    }

    @Operation(
        summary = "지수 정보 요약 목록 조회",
        description = "지수 ID, 분류, 이름만 포함한 전체 지수 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "지수 정보 요약 목록 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = IndexInfoSummaryDto.class)))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/summaries")
    public ResponseEntity<List<IndexInfoSummaryDto>> summaries() {
        return ResponseEntity.ok(
                indexInfoService.summaries()
        );
    }
}
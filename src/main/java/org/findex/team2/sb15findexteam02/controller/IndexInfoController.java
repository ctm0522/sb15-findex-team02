package org.findex.team2.sb15findexteam02.controller;

import org.findex.team2.sb15findexteam02.dto.request.IndexInfoCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.IndexInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/index-infos")
public class IndexInfoController {

    // 1. 지수 정보 목록 조회
    @GetMapping
    public ResponseEntity<CursorPageResponseIndexInfoDto> findAll(
            @RequestParam(required = false) String indexClassification,
            @RequestParam(required = false) String indexName,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(required = false) Long idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer size
    ) {
        return null;
    }


    // 2. 지수 정보 등록
    @PostMapping
    public ResponseEntity<IndexInfoDto> create(
            @RequestBody IndexInfoCreateRequest request
    ) {
        return null;
    }


    // 3. 지수 정보 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<IndexInfoDto> find(
            @PathVariable Long id
    ) {
        return null;
    }


    // 4. 지수 정보 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        return null;
    }


    // 5. 지수 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<IndexInfoDto> patch(
            @PathVariable Long id,
            @RequestBody IndexInfoUpdateRequest request
    ) {
        return null;
    }


    // 6. 지수 정보 요약 목록 조회
    @GetMapping("/summaries")
    public ResponseEntity<List<SummaryDto>> summaries() {
        return null;
    }
}
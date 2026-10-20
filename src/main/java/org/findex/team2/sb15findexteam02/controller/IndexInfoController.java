package org.findex.team2.sb15findexteam02.controller;

import org.findex.team2.sb15findexteam02.dto.request.IndexInfoCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.IndexInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/index-infos")
public class IndexInfoController {

    private final IndexInfoService indexInfoService;

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
        return ResponseEntity.ok(
                indexInfoService.findAll(
                        indexClassification
                        indexName,
                        favorite,
                        idAfter,
                        cursor,
                        sortField,
                        sortDirection,
                        size
                )
        );
    }

    @PostMapping
    public ResponseEntity<IndexInfoDto> create(
            @RequestBody IndexInfoCreateRequest request
    ) {
        return ResponseEntity.ok(
                indexInfoService.create(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndexInfoDto> find(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                indexInfoService.find(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        indexInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IndexInfoDto> patch(
            @PathVariable Long id,
            @RequestBody IndexInfoUpdateRequest request
    ) {
        return ResponseEntity.ok(
                indexInfoService.update(id, request)
        )
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<SummaryDto>> summaries() {
        return ResponseEntity.ok(
                indexInfoService.summaries()
        )
    }
}
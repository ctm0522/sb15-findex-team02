package org.findex.team2.sb15findexteam02.controller;

import lombok.AllArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSearchRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.CursorPageResponseIndexDataDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexChartDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexDataDto;
import org.findex.team2.sb15findexteam02.dto.response.RankedIndexPerformanceDto;
import org.findex.team2.sb15findexteam02.model.PeriodType;
import org.findex.team2.sb15findexteam02.model.SourceType;
import org.findex.team2.sb15findexteam02.service.IndexDataService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/index-data")
public class IndexDataController {

    private final IndexDataService indexDataService;

    @GetMapping
    public ResponseEntity<CursorPageResponseIndexDataDto> findAll(
            @ModelAttribute IndexDataSearchRequest request
            ) {
        return ResponseEntity.ok(indexDataService.findAll(request));
    }

    @PostMapping
    public ResponseEntity<IndexDataDto> create(
            @RequestBody IndexDataCreateRequest request,
            @RequestParam SourceType sourceType
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(indexDataService.create(request, sourceType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<void> delete(
            @PathVariable Long id
    ) {
        indexDataService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IndexDataDto> patch(
            @PathVariable Long id,
            @RequestBody IndexDataUpdateRequest request,
            @RequestParam SourceType sourceType
    ) {
        return ResponseEntity.ok(indexDataService.update(id, request, sourceType));
    }

    @GetMapping("/{id}/chart")
    public ResponseEntity<IndexChartDto> Chart(
            @PathVariable Long id,
            @RequestParam(defaultValue = "DAILY") PeriodType periodType
    ) {
        return ResponseEntity.ok(indexDataService.Chart(id, periodType));
    }

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

    @GetMapping("/performance/favorite")
    public ResponseEntity<List<RankedIndexPerformanceDto>> favorite(
            @RequestParam(defaultValue = "DAILY") PeriodType periodType
    ) {
        return ResponseEntity.ok(
                indexDataService.favorite(periodType)
        );
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> toCSV(
            @ModelAttribute IndexDataSearchRequest request
    ) {
        byte[] csv = indexDataService.toCSV(request);

        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"index-data.csv\""
        )
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}

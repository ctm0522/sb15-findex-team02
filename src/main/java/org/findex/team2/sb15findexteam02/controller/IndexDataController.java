package org.findex.team2.sb15findexteam02.controller;

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
            @RequestBody IndexDataCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(indexDataService.create(request, SourceType.USER));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        indexDataService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IndexDataDto> patch(
            @PathVariable Long id,
            @RequestBody IndexDataUpdateRequest request
    ) {
        return ResponseEntity.ok(indexDataService.update(id, request, SourceType.USER));
    }

    @GetMapping("/{id}/chart")
    public ResponseEntity<IndexChartDto> Chart(
            @PathVariable Long id,
            @RequestParam(defaultValue = "MONTHLY") ChartPeriodType periodType
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
    public ResponseEntity<List<IndexPerformanceDto>> favorite(
            @RequestParam(defaultValue = "DAILY") PeriodType periodType
    ) {
        return ResponseEntity.ok(
                indexDataService.favorite(periodType)
        );
    }

    @GetMapping("/export/csv")
    public ResponseEntity<?> toCSV(
            @ModelAttribute IndexDataSearchRequest request
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

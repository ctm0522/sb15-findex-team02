package org.findex.team2.sb15findexteam02.controller;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSyncRequest;
import org.findex.team2.sb15findexteam02.dto.request.SyncjobSearchRequest;
import org.findex.team2.sb15findexteam02.dto.response.CursorPageResponseSyncJobDto;
import org.findex.team2.sb15findexteam02.dto.response.SyncJobDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sync-jobs")
@RequiredArgsConstructor
public class SyncJobController {

    private final SyncJobService syncJobService;

    @PostMapping("/index-infos")
    public ResponseEntity<List<SyncJobDto>> syncIndexInfos() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(syncJobService.syncIndexInfos());
    }

    @PostMapping("/index-data")
    public ResponseEntity<List<SyncJobDto>>syncIndexData(
            @RequestBody IndexDataSyncRequest request
    ){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(syncJobService.syncInxdexData(request));
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseSyncJobDto> findAll(
            @ModelAttribute SyncjobSearchRequest request
    ) {
        return ResponseEntity.ok(
                syncJobService.findAll(request)
        );
    }


}
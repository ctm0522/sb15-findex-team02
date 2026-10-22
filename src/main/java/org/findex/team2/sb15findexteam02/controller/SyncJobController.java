package org.findex.team2.sb15findexteam02.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.client.PublicDataClient;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSyncRequest;
import org.findex.team2.sb15findexteam02.dto.request.SyncjobSearchRequest;
import org.findex.team2.sb15findexteam02.dto.response.CursorPageResponseSyncJobDto;
import org.findex.team2.sb15findexteam02.dto.response.SyncJobDto;
import org.findex.team2.sb15findexteam02.service.SyncJobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sync-jobs")
@RequiredArgsConstructor
public class SyncJobController {

    private final SyncJobService syncJobService;
    private final PublicDataClient publicDataClient;

    @PostMapping("/index-infos")
    public ResponseEntity<List<SyncJobDto>> syncIndexInfos(HttpServletRequest request) {
        //워커관련
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(publicDataClient.fetchIndexInfo(request.getRemoteAddr()));
    }

    @PostMapping("/index-data")
    public ResponseEntity<List<SyncJobDto>>syncIndexData(
            @RequestBody IndexDataSyncRequest request,
            HttpServletRequest httpServletRequest
    ){
        String worker = httpServletRequest.getRemoteAddr();

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(syncJobService.convertToIndexDataClientDtoDto(request, worker));
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

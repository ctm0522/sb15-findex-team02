package org.findex.team2.sb15findexteam02.controller;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.AutoSyncConfigSearchRequest;
import org.findex.team2.sb15findexteam02.dto.request.AutoSyncConfigUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.AutoSyncConfigDto;
import org.findex.team2.sb15findexteam02.dto.response.CursorPageResponseAutoSyncConfigDto;
import org.findex.team2.sb15findexteam02.service.basic.AutoSyncConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auto-sync-config")
@RequiredArgsConstructor
public class AutoSyncConfigController {

    private final AutoSyncConfigService autoSyncConfigService;

    @PatchMapping("/{id}")
    public ResponseEntity<AutoSyncConfigDto> update(
            @PathVariable Long id,
            @RequestBody AutoSyncConfigUpdateRequest request
    ) {
        return ResponseEntity.ok(
                autoSyncConfigService.update(id, request)
        );
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseAutoSyncConfigDto> findAll(
            @ModelAttribute AutoSyncConfigSearchRequest request
    ) {
        return ResponseEntity.ok(
                autoSyncConfigService.findAll(request)
        );
    }
}

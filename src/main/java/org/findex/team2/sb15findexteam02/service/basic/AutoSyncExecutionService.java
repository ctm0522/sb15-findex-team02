package org.findex.team2.sb15findexteam02.service.basic;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSyncRequest;
import org.findex.team2.sb15findexteam02.dto.response.SyncJobDto;
import org.findex.team2.sb15findexteam02.model.AutoSyncConfig;
import org.findex.team2.sb15findexteam02.model.ResultType;
import org.findex.team2.sb15findexteam02.repository.AutoSyncConfigRepository;
import org.findex.team2.sb15findexteam02.service.SyncJobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoSyncExecutionService {
    private final AutoSyncConfigRepository repository;
    private final SyncJobService syncJobService;

    @Transactional
    public void execute(Long configId, LocalDate targetDate, ZoneId zone) {
        AutoSyncConfig config = repository.findById(configId).orElse(null);
        if (config == null || !config.isEnabled()) {
            return;
        }

        IndexDataSyncRequest request = new IndexDataSyncRequest(
                List.of(config.getIndexInfo().getId()), targetDate, targetDate);
        List<SyncJobDto> results = syncJobService.convertToIndexDataClientDtoDto(request, "SYSTEM");
        if (!results.isEmpty() && results.stream()
                .allMatch(result -> ResultType.SUCCESS.name().equals(result.getResult()))) {
            repository.updateLastSyncedAt(configId, LocalDateTime.now(zone));
        }
    }
}

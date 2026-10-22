package org.findex.team2.sb15findexteam02.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.findex.team2.sb15findexteam02.repository.AutoSyncConfigRepository;
import org.findex.team2.sb15findexteam02.service.basic.AutoSyncExecutionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Component
public class AutoSyncScheduler {
    private final AutoSyncConfigRepository repository;
    private final AutoSyncExecutionService executionService;
    private final ZoneId zone;

    public AutoSyncScheduler(AutoSyncConfigRepository repository,
                             AutoSyncExecutionService executionService,
                             @Value("${findex.auto-sync.zone:Asia/Seoul}") String zone) {
        this.repository = repository;
        this.executionService = executionService;
        this.zone = ZoneId.of(zone);
    }

    @Scheduled(cron = "${findex.auto-sync.cron:0 0 6 * * *}",
            zone = "${findex.auto-sync.zone:Asia/Seoul}")
    public void syncEnabledConfigs() {
        LocalDate targetDate = LocalDate.now(zone).minusDays(1);
        for (Long configId : repository.findEnabledIds()) {
            try {
                executionService.execute(configId, targetDate, zone);
            } catch (Exception e) {
                log.error("자동 동기화 실패: configId={}, targetDate={}", configId, targetDate, e);
            }
        }
    }
}

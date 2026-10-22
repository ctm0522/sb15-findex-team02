package org.findex.team2.sb15findexteam02.scheduler;

import org.findex.team2.sb15findexteam02.repository.AutoSyncConfigRepository;
import org.findex.team2.sb15findexteam02.service.basic.AutoSyncExecutionService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AutoSyncSchedulerTest {
    private final AutoSyncConfigRepository repository = mock(AutoSyncConfigRepository.class);
    private final AutoSyncExecutionService service = mock(AutoSyncExecutionService.class);
    private final AutoSyncScheduler scheduler = new AutoSyncScheduler(repository, service, "Asia/Seoul");

    @Test
    void executesEnabledConfigsAndContinuesAfterFailure() {
        when(repository.findEnabledIds()).thenReturn(List.of(1L, 3L));
        doThrow(new IllegalStateException("sync failed"))
                .when(service).execute(eq(1L), any(LocalDate.class), any(ZoneId.class));

        LocalDate before = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
        scheduler.syncEnabledConfigs();
        LocalDate after = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);

        verify(service).execute(eq(1L), argThat(date -> date.equals(before) || date.equals(after)),
                eq(ZoneId.of("Asia/Seoul")));
        verify(service).execute(eq(3L), argThat(date -> date.equals(before) || date.equals(after)),
                eq(ZoneId.of("Asia/Seoul")));
        verifyNoMoreInteractions(service);
    }

    @Test
    void skipsExecutionWhenNoConfigsAreEnabled() {
        when(repository.findEnabledIds()).thenReturn(List.of());
        scheduler.syncEnabledConfigs();
        verifyNoInteractions(service);
    }
}

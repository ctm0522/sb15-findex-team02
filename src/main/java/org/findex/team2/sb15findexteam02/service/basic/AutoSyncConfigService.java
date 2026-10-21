package org.findex.team2.sb15findexteam02.service.basic;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.AutoSyncConfigUpdateRequest;
import org.findex.team2.sb15findexteam02.dto.response.AutoSyncConfigDto;
import org.findex.team2.sb15findexteam02.model.AutoSyncConfig;
import org.findex.team2.sb15findexteam02.repository.AutoSyncConfigRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AutoSyncConfigService {

    private final AutoSyncConfigRepository autoSyncConfigRepository;

    @Transactional
    public AutoSyncConfigDto update(
            Long id,
            AutoSyncConfigUpdateRequest request
    ) {
        int updated = autoSyncConfigRepository.updateEnabled(
                id, request.getEnabled()
        );

        if (updated == 0) {
            throw new NoSuchElementException( "자동 연동 설정을 찾을 수 없습니다. id = " + id);
        }

        AutoSyncConfig config = autoSyncConfigRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException("자동 연동 설정을 찾을 수 없습니다. id = " + id)
        );

        return new AutoSyncConfigDto(
                config.getId(),
                config.getIndexInfo().getId(),
                config.getIndexInfo().getIndexCategory().getCategoryName(),
                config.getIndexInfo().getIndexName(),
                config.isEnabled()
        );
    }
}

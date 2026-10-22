
package org.findex.team2.sb15findexteam02.repository;

import org.findex.team2.sb15findexteam02.model.AutoSyncConfig;

import java.util.List;

public interface AutoSyncConfigCursorRepository {

    List<AutoSyncConfig> searchCursor(
            Long indexInfoId,
            Boolean enabled,
            String sortField,
            String sortDirection,
            String cursorValue,
            Long cursorId,
            int limit
    );

    long countByFilters(Long indexInfoId, Boolean enabled);
}

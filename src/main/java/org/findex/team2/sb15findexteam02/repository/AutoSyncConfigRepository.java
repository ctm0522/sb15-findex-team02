package org.findex.team2.sb15findexteam02.repository;

import org.findex.team2.sb15findexteam02.model.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AutoSyncConfigRepository
        extends JpaRepository<AutoSyncConfig, Long>,
        AutoSyncConfigCursorRepository {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update AutoSyncConfig a
        set a.enabled = :enabled
        where a.id = :id
    """)
    int updateEnabled(
            @Param("id") Long id,
            @Param("enabled") Boolean enabled
    );
}

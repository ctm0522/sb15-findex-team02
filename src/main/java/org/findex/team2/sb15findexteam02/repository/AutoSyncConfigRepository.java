package org.findex.team2.sb15findexteam02.repository;

import org.findex.team2.sb15findexteam02.model.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long> {

    @Modifying
    @Query("""
        update AutoSyncConfig a
        set a.enabled = :enabled
        where a.id = :id
    """)
    int updateEnabled(
            @Param("id") Long id,
            @Param("enabled") Boolean enabled
    );

    @Query("""
        select a
        from AutoSyncConfig a
        where (:indexInfoId is null or a.indexInfo.id = :indexInfoId)
        and (:enabled is null or a.enabled = :enabled)
    """)
    List<AutoSyncConfig> search(
            @Param("indexInfoId") Long indexInfoId,
            @Param("enabled") Boolean enabled
    );
}

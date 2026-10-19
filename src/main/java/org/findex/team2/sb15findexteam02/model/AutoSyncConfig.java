package org.findex.team2.sb15findexteam02.model;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

public class AutoSyncConfig extends BaseUpdatableModel{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_category_id",nullable = false)
    private IndexInfo indexInfo;

    @Column(nullable = false)
    private boolean enabled;

    @Column(length = 10, nullable = false)
    private String schedule;

    @Column
    private LocalDateTime lastSyncedAt;
}

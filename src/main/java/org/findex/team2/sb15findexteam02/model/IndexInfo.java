package org.findex.team2.sb15findexteam02.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "index_infos")
@Entity
@Getter
public class IndexInfo extends BaseUpdatableModel{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_category_id",nullable = false)
    private IndexCategory indexCategory;

    @Column(length = 20, nullable = false)
    private String indexName;

    @Column(nullable = false)
    private int employedItemsCount;

    @Column(nullable = false)
    private LocalDate basePointTime;

    @Column(precision = 20, scale = 6, nullable = false)
    private BigDecimal baseIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SourceType sourceType;

    @Column(nullable = false)
    private boolean favorite;


}

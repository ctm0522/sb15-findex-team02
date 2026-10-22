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

    @Column(length = 100, nullable = false)
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

    protected IndexInfo(){

    }

    public IndexInfo(IndexCategory indexCategory, String indexName, int employedItemsCount,
                     LocalDate basePointTime, BigDecimal baseIndex, SourceType sourceType, boolean favorite){
        this.indexCategory = indexCategory;
        this.indexName = indexName;
        this.employedItemsCount = employedItemsCount;
        this.basePointTime = basePointTime;
        this.baseIndex = baseIndex;
        this.sourceType = sourceType;
        this.favorite = favorite;
    }

    public void update(int employedItemsCount, LocalDate basePointTime, BigDecimal baseIndex, Boolean favorite, SourceType sourceType){
        this.employedItemsCount = employedItemsCount;
        this.basePointTime = basePointTime;
        this.baseIndex = baseIndex;
        this.favorite = favorite;
        this.sourceType = sourceType;
    }


    public void setIndexCategory(IndexCategory indexCategory) {
        this.indexCategory = indexCategory;
    }
}

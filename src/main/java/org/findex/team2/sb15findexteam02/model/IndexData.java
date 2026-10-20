package org.findex.team2.sb15findexteam02.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "index_data")
@Entity
@Getter
public class IndexData extends BaseUpdatableModel{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id",nullable = false)
    private IndexInfo indexInfo;

    @Column(nullable = false)
    private LocalDate baseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SourceType sourceType;

    @Column(precision = 20, scale = 6, nullable = false)
    private BigDecimal marketPrice;

    @Column(precision = 20, scale = 6, nullable = false)
    private BigDecimal closingPrice;

    @Column(precision = 20, scale = 6, nullable = false)
    private BigDecimal highPrice;

    @Column(precision = 20, scale = 6, nullable = false)
    private BigDecimal lowPrice;

    @Column(precision = 20, scale = 6, nullable = false)
    private BigDecimal versus;

    @Column(precision = 15, scale = 6, nullable = false)
    private BigDecimal fluctuationRate;

    @Column(nullable = false)
    private Long tradingQuantity;

    @Column(nullable = false)
    private Long tradingPrice;

    @Column(nullable = false)
    private Long marketTotalAmount;

    protected IndexData(){

    }


}

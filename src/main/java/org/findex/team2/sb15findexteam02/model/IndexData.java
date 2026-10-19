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
    private BigDecimal high_price;

    @Column(precision = 20, scale = 6, nullable = false)
    private BigDecimal low_price;

    @Column(precision = 20, scale = 6, nullable = false)
    private BigDecimal versus;

    @Column(precision = 15, scale = 6, nullable = false)
    private BigDecimal fluctuation_rate;

    @Column(nullable = false)
    private int tradingQuantity;

    @Column(precision = 25, scale = 3, nullable = false)
    private BigDecimal tradingPrice;

    @Column(precision = 27, scale = 3)
    private BigDecimal marketTotalAmount;

    protected IndexData(){

    }


}

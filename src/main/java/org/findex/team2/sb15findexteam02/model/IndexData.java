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

    public IndexData(IndexInfo indexInfo, LocalDate baseDate, BigDecimal marketPrice, BigDecimal closingPrice,
                        BigDecimal highPrice, BigDecimal lowPrice,BigDecimal versus, BigDecimal fluctuationRate, Long tradingQuantity,
                        Long tradingPrice, Long marketTotalAmount, SourceType sourceType){
        this.indexInfo = indexInfo;
        this.baseDate = baseDate;
        this.marketPrice = marketPrice;
        this.closingPrice = closingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.versus = versus;
        this.fluctuationRate = fluctuationRate;
        this.tradingQuantity = tradingQuantity;
        this.tradingPrice = tradingPrice;
        this.marketTotalAmount = marketTotalAmount;
        this.sourceType = sourceType;

    }

    public void update(BigDecimal marketPrice, BigDecimal closingPrice, BigDecimal highPrice, BigDecimal lowPrice,
                       BigDecimal versus, BigDecimal fluctuationRate, Long tradingQuantity, Long tradingPrice, Long marketTotalAmount, SourceType sourceType){
        this.marketPrice = marketPrice;
        this.closingPrice = closingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.versus = versus;
        this.fluctuationRate = fluctuationRate;
        this.tradingQuantity = tradingQuantity;
        this.tradingPrice = tradingPrice;
        this.marketTotalAmount =marketTotalAmount;
        this.sourceType=sourceType;
    }

}

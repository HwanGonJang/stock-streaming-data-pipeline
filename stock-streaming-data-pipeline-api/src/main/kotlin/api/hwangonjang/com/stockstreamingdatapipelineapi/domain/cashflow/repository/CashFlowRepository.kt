package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.repository

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.entity.CashFlow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface CashFlowRepository : JpaRepository<CashFlow, Long> {
    
    fun findBySymbolOrderByFiscalDateEndingDesc(symbol: String, pageable: Pageable): Page<CashFlow>
    
    fun findBySymbolAndIsQuarterlyOrderByFiscalDateEndingDesc(
        symbol: String, 
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<CashFlow>
    
    fun findBySymbolAndFiscalDateEndingBetweenOrderByFiscalDateEndingDesc(
        symbol: String,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<CashFlow>
    
    fun findBySymbolAndIsQuarterlyAndFiscalDateEndingBetweenOrderByFiscalDateEndingDesc(
        symbol: String,
        isQuarterly: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<CashFlow>
    
    @Query("SELECT i FROM CashFlow i WHERE i.symbol = :symbol AND YEAR(i.fiscalDateEnding) = :year ORDER BY i.fiscalDateEnding DESC")
    fun findBySymbolAndYearOrderByFiscalDateEndingDesc(
        @Param("symbol") symbol: String,
        @Param("year") year: Int,
        pageable: Pageable
    ): Page<CashFlow>
    
    @Query("SELECT i FROM CashFlow i WHERE i.symbol = :symbol AND YEAR(i.fiscalDateEnding) = :year AND i.isQuarterly = :isQuarterly ORDER BY i.fiscalDateEnding DESC")
    fun findBySymbolAndYearAndIsQuarterlyOrderByFiscalDateEndingDesc(
        @Param("symbol") symbol: String,
        @Param("year") year: Int,
        @Param("isQuarterly") isQuarterly: Boolean,
        pageable: Pageable
    ): Page<CashFlow>
    
    @Query("SELECT i FROM CashFlow i WHERE i.symbol = :symbol AND YEAR(i.fiscalDateEnding) = :year AND QUARTER(i.fiscalDateEnding) = :quarter ORDER BY i.fiscalDateEnding DESC")
    fun findBySymbolAndYearAndQuarterOrderByFiscalDateEndingDesc(
        @Param("symbol") symbol: String,
        @Param("year") year: Int,
        @Param("quarter") quarter: Int,
        pageable: Pageable
    ): Page<CashFlow>
    
    fun findTopBySymbolAndIsQuarterlyOrderByFiscalDateEndingDesc(
        symbol: String,
        isQuarterly: Boolean
    ): CashFlow?

    @Query("SELECT i FROM CashFlow i WHERE i.symbol = :symbol AND i.netIncome > :minNetIncome ORDER BY i.fiscalDateEnding DESC")
    fun findBySymbolAndNetIncomeGreaterThanOrderByFiscalDateEndingDesc(
        @Param("symbol") symbol: String,
        @Param("minNetIncome") minNetIncome: Long,
        pageable: Pageable
    ): Page<CashFlow>
    
    @Query("SELECT i FROM CashFlow i WHERE YEAR(i.fiscalDateEnding) = :year ORDER BY i.fiscalDateEnding DESC")
    fun findByYearOrderByFiscalDateEndingDesc(
        @Param("year") year: Int,
        pageable: Pageable
    ): Page<CashFlow>
    
    @Query("SELECT i FROM CashFlow i WHERE YEAR(i.fiscalDateEnding) = :year AND i.isQuarterly = :isQuarterly ORDER BY i.fiscalDateEnding DESC")
    fun findByYearAndIsQuarterlyOrderByFiscalDateEndingDesc(
        @Param("year") year: Int,
        @Param("isQuarterly") isQuarterly: Boolean,
        pageable: Pageable
    ): Page<CashFlow>
}
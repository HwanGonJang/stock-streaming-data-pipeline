package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.repository

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.entity.BalanceSheet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface BalanceSheetRepository : JpaRepository<BalanceSheet, Long> {
    
    fun findBySymbolOrderByFiscalDateEndingDesc(symbol: String, pageable: Pageable): Page<BalanceSheet>
    
    fun findBySymbolAndIsQuarterlyOrderByFiscalDateEndingDesc(
        symbol: String, 
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<BalanceSheet>
    
    fun findBySymbolAndFiscalDateEndingBetweenOrderByFiscalDateEndingDesc(
        symbol: String,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<BalanceSheet>
    
    fun findBySymbolAndIsQuarterlyAndFiscalDateEndingBetweenOrderByFiscalDateEndingDesc(
        symbol: String,
        isQuarterly: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<BalanceSheet>
    
    @Query("SELECT i FROM BalanceSheet i WHERE i.symbol = :symbol AND YEAR(i.fiscalDateEnding) = :year ORDER BY i.fiscalDateEnding DESC")
    fun findBySymbolAndYearOrderByFiscalDateEndingDesc(
        @Param("symbol") symbol: String,
        @Param("year") year: Int,
        pageable: Pageable
    ): Page<BalanceSheet>
    
    @Query("SELECT i FROM BalanceSheet i WHERE i.symbol = :symbol AND YEAR(i.fiscalDateEnding) = :year AND i.isQuarterly = :isQuarterly ORDER BY i.fiscalDateEnding DESC")
    fun findBySymbolAndYearAndIsQuarterlyOrderByFiscalDateEndingDesc(
        @Param("symbol") symbol: String,
        @Param("year") year: Int,
        @Param("isQuarterly") isQuarterly: Boolean,
        pageable: Pageable
    ): Page<BalanceSheet>
    
    @Query("SELECT i FROM BalanceSheet i WHERE i.symbol = :symbol AND YEAR(i.fiscalDateEnding) = :year AND QUARTER(i.fiscalDateEnding) = :quarter ORDER BY i.fiscalDateEnding DESC")
    fun findBySymbolAndYearAndQuarterOrderByFiscalDateEndingDesc(
        @Param("symbol") symbol: String,
        @Param("year") year: Int,
        @Param("quarter") quarter: Int,
        pageable: Pageable
    ): Page<BalanceSheet>
    
    fun findTopBySymbolAndIsQuarterlyOrderByFiscalDateEndingDesc(
        symbol: String,
        isQuarterly: Boolean
    ): BalanceSheet?
    
    @Query("SELECT i FROM BalanceSheet i WHERE YEAR(i.fiscalDateEnding) = :year ORDER BY i.fiscalDateEnding DESC")
    fun findByYearOrderByFiscalDateEndingDesc(
        @Param("year") year: Int,
        pageable: Pageable
    ): Page<BalanceSheet>
    
    @Query("SELECT i FROM BalanceSheet i WHERE YEAR(i.fiscalDateEnding) = :year AND i.isQuarterly = :isQuarterly ORDER BY i.fiscalDateEnding DESC")
    fun findByYearAndIsQuarterlyOrderByFiscalDateEndingDesc(
        @Param("year") year: Int,
        @Param("isQuarterly") isQuarterly: Boolean,
        pageable: Pageable
    ): Page<BalanceSheet>
}
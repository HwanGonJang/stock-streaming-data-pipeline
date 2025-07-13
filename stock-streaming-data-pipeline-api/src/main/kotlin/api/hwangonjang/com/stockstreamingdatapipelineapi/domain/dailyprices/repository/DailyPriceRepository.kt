package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.repository

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.entity.DailyPrice
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyPriceRepository : JpaRepository<DailyPrice, Long> {
    
    fun findBySymbolOrderByDateDesc(symbol: String, pageable: Pageable): Page<DailyPrice>
    
    fun findBySymbolAndDateBetweenOrderByDateDesc(
        symbol: String, 
        startDate: LocalDate, 
        endDate: LocalDate,
        pageable: Pageable
    ): Page<DailyPrice>
    
    fun findBySymbolAndDateBetweenOrderByDateAsc(
        symbol: String, 
        startDate: LocalDate, 
        endDate: LocalDate,
        pageable: Pageable
    ): Page<DailyPrice>
    
    fun findByDateBetweenOrderByDateDesc(
        startDate: LocalDate, 
        endDate: LocalDate,
        pageable: Pageable
    ): Page<DailyPrice>
    
    @Query("SELECT dp FROM DailyPrice dp WHERE YEAR(dp.date) = :year ORDER BY dp.date DESC")
    fun findByYearOrderByDateDesc(@Param("year") year: Int, pageable: Pageable): Page<DailyPrice>
    
    @Query("SELECT dp FROM DailyPrice dp WHERE dp.symbol = :symbol AND YEAR(dp.date) = :year ORDER BY dp.date DESC")
    fun findBySymbolAndYearOrderByDateDesc(
        @Param("symbol") symbol: String,
        @Param("year") year: Int, 
        pageable: Pageable
    ): Page<DailyPrice>
    
    @Query("SELECT dp FROM DailyPrice dp WHERE dp.symbol = :symbol AND YEAR(dp.date) = :year AND QUARTER(dp.date) = :quarter ORDER BY dp.date DESC")
    fun findBySymbolAndYearAndQuarterOrderByDateDesc(
        @Param("symbol") symbol: String,
        @Param("year") year: Int,
        @Param("quarter") quarter: Int,
        pageable: Pageable
    ): Page<DailyPrice>
    
    @Query("SELECT dp FROM DailyPrice dp WHERE dp.symbol = :symbol AND YEAR(dp.date) = :year AND MONTH(dp.date) = :month ORDER BY dp.date DESC")
    fun findBySymbolAndYearAndMonthOrderByDateDesc(
        @Param("symbol") symbol: String,
        @Param("year") year: Int,
        @Param("month") month: Int,
        pageable: Pageable
    ): Page<DailyPrice>
    
    fun findTopBySymbolOrderByDateDesc(symbol: String): DailyPrice?
    
    @Query("SELECT dp FROM DailyPrice dp WHERE dp.symbol = :symbol AND dp.volume > :minVolume ORDER BY dp.date DESC")
    fun findBySymbolAndVolumeGreaterThanOrderByDateDesc(
        @Param("symbol") symbol: String,
        @Param("minVolume") minVolume: Long,
        pageable: Pageable
    ): Page<DailyPrice>
}
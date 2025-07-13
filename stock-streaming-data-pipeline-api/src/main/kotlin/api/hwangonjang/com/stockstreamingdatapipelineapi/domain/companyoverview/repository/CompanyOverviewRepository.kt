package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.repository

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.entity.CompanyOverview
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface CompanyOverviewRepository : JpaRepository<CompanyOverview, String> {
    
    fun findBySymbol(symbol: String): CompanyOverview?
    
    fun findBySector(sector: String, pageable: Pageable): Page<CompanyOverview>
    
    fun findByIndustry(industry: String, pageable: Pageable): Page<CompanyOverview>
    
    fun findByCountry(country: String, pageable: Pageable): Page<CompanyOverview>
    
    fun findBySectorAndIndustry(sector: String, industry: String, pageable: Pageable): Page<CompanyOverview>
    
    @Query("SELECT co FROM CompanyOverview co WHERE co.marketCapitalization >= :minMarketCap ORDER BY co.marketCapitalization DESC")
    fun findByMarketCapitalizationGreaterThanEqualOrderByMarketCapitalizationDesc(
        @Param("minMarketCap") minMarketCap: Long,
        pageable: Pageable
    ): Page<CompanyOverview>
    
    @Query("SELECT co FROM CompanyOverview co WHERE co.peRatio BETWEEN :minPE AND :maxPE ORDER BY co.peRatio ASC")
    fun findByPeRatioBetweenOrderByPeRatioAsc(
        @Param("minPE") minPE: BigDecimal,
        @Param("maxPE") maxPE: BigDecimal,
        pageable: Pageable
    ): Page<CompanyOverview>
    
    @Query("SELECT co FROM CompanyOverview co WHERE co.dividendYield >= :minDividendYield ORDER BY co.dividendYield DESC")
    fun findByDividendYieldGreaterThanEqualOrderByDividendYieldDesc(
        @Param("minDividendYield") minDividendYield: BigDecimal,
        pageable: Pageable
    ): Page<CompanyOverview>
    
    @Query("SELECT co FROM CompanyOverview co WHERE co.returnOnEquityTtm >= :minROE ORDER BY co.returnOnEquityTtm DESC")
    fun findByReturnOnEquityTtmGreaterThanEqualOrderByReturnOnEquityTtmDesc(
        @Param("minROE") minROE: BigDecimal,
        pageable: Pageable
    ): Page<CompanyOverview>
    
    @Query("SELECT co FROM CompanyOverview co WHERE co.beta BETWEEN :minBeta AND :maxBeta ORDER BY co.beta ASC")
    fun findByBetaBetweenOrderByBetaAsc(
        @Param("minBeta") minBeta: BigDecimal,
        @Param("maxBeta") maxBeta: BigDecimal,
        pageable: Pageable
    ): Page<CompanyOverview>
    
    @Query("SELECT co FROM CompanyOverview co WHERE co.sector = :sector ORDER BY co.marketCapitalization DESC")
    fun findBySectorOrderByMarketCapitalizationDesc(
        @Param("sector") sector: String,
        pageable: Pageable
    ): Page<CompanyOverview>
    
    @Query("SELECT co FROM CompanyOverview co WHERE co.industry = :industry ORDER BY co.marketCapitalization DESC")
    fun findByIndustryOrderByMarketCapitalizationDesc(
        @Param("industry") industry: String,
        pageable: Pageable
    ): Page<CompanyOverview>
    
    @Query("SELECT DISTINCT co.sector FROM CompanyOverview co WHERE co.sector IS NOT NULL ORDER BY co.sector")
    fun findDistinctSectors(): List<String>
    
    @Query("SELECT DISTINCT co.industry FROM CompanyOverview co WHERE co.industry IS NOT NULL ORDER BY co.industry")
    fun findDistinctIndustries(): List<String>
    
    @Query("SELECT DISTINCT co.country FROM CompanyOverview co WHERE co.country IS NOT NULL ORDER BY co.country")
    fun findDistinctCountries(): List<String>
}
package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.service

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.dto.CompanyOverviewDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.exception.CompanyOverviewNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.repository.CompanyOverviewRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CompanyOverviewService(
    private val companyOverviewRepository: CompanyOverviewRepository
) {
    
    fun getCompanyOverviewBySymbol(symbol: String): CompanyOverviewDto {
        val companyOverview = companyOverviewRepository.findBySymbol(symbol)
            ?: throw CompanyOverviewNotFoundException("Company overview not found for symbol: $symbol")
        return CompanyOverviewDto.from(companyOverview)
    }
    
    fun getCompanyOverviewsBySector(sector: String, pageable: Pageable): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findBySector(sector, pageable)
            .map { CompanyOverviewDto.from(it) }
    }
    
    fun getCompanyOverviewsByIndustry(industry: String, pageable: Pageable): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findByIndustry(industry, pageable)
            .map { CompanyOverviewDto.from(it) }
    }
    
    fun getCompanyOverviewsByCountry(country: String, pageable: Pageable): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findByCountry(country, pageable)
            .map { CompanyOverviewDto.from(it) }
    }
    
    fun getCompanyOverviewsBySectorAndIndustry(
        sector: String, 
        industry: String, 
        pageable: Pageable
    ): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findBySectorAndIndustry(sector, industry, pageable)
            .map { CompanyOverviewDto.from(it) }
    }
    
    fun getCompanyOverviewsByMarketCap(minMarketCap: Long, pageable: Pageable): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findByMarketCapitalizationGreaterThanEqualOrderByMarketCapitalizationDesc(
            minMarketCap, pageable
        ).map { CompanyOverviewDto.from(it) }
    }
    
    fun getCompanyOverviewsByPeRatio(
        minPE: BigDecimal, 
        maxPE: BigDecimal, 
        pageable: Pageable
    ): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findByPeRatioBetweenOrderByPeRatioAsc(minPE, maxPE, pageable)
            .map { CompanyOverviewDto.from(it) }
    }
    
    fun getCompanyOverviewsByDividendYield(
        minDividendYield: BigDecimal, 
        pageable: Pageable
    ): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findByDividendYieldGreaterThanEqualOrderByDividendYieldDesc(
            minDividendYield, pageable
        ).map { CompanyOverviewDto.from(it) }
    }
    
    fun getCompanyOverviewsByROE(minROE: BigDecimal, pageable: Pageable): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findByReturnOnEquityTtmGreaterThanEqualOrderByReturnOnEquityTtmDesc(
            minROE, pageable
        ).map { CompanyOverviewDto.from(it) }
    }
    
    fun getCompanyOverviewsByBeta(
        minBeta: BigDecimal, 
        maxBeta: BigDecimal, 
        pageable: Pageable
    ): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findByBetaBetweenOrderByBetaAsc(minBeta, maxBeta, pageable)
            .map { CompanyOverviewDto.from(it) }
    }
    
    fun getTopCompaniesBySector(sector: String, pageable: Pageable): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findBySectorOrderByMarketCapitalizationDesc(sector, pageable)
            .map { CompanyOverviewDto.from(it) }
    }
    
    fun getTopCompaniesByIndustry(industry: String, pageable: Pageable): Page<CompanyOverviewDto> {
        return companyOverviewRepository.findByIndustryOrderByMarketCapitalizationDesc(industry, pageable)
            .map { CompanyOverviewDto.from(it) }
    }
    
    fun getDistinctSectors(): List<String> {
        return companyOverviewRepository.findDistinctSectors()
    }
    
    fun getDistinctIndustries(): List<String> {
        return companyOverviewRepository.findDistinctIndustries()
    }
    
    fun getDistinctCountries(): List<String> {
        return companyOverviewRepository.findDistinctCountries()
    }
}
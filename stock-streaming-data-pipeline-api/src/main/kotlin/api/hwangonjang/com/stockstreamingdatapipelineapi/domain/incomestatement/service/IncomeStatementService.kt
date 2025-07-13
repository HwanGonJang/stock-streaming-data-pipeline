package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.service

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.dto.IncomeStatementDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.exception.IncomeStatementNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.repository.IncomeStatementRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class IncomeStatementService(
    private val incomeStatementRepository: IncomeStatementRepository
) {
    
    fun getIncomeStatementsBySymbol(symbol: String, pageable: Pageable): Page<IncomeStatementDto> {
        return incomeStatementRepository.findBySymbolOrderByFiscalDateEndingDesc(symbol, pageable)
            .map { IncomeStatementDto.from(it) }
    }
    
    fun getIncomeStatementsBySymbolAndType(
        symbol: String,
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<IncomeStatementDto> {
        return incomeStatementRepository.findBySymbolAndIsQuarterlyOrderByFiscalDateEndingDesc(
            symbol, isQuarterly, pageable
        ).map { IncomeStatementDto.from(it) }
    }
    
    fun getIncomeStatementsBySymbolAndDateRange(
        symbol: String,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<IncomeStatementDto> {
        return incomeStatementRepository.findBySymbolAndFiscalDateEndingBetweenOrderByFiscalDateEndingDesc(
            symbol, startDate, endDate, pageable
        ).map { IncomeStatementDto.from(it) }
    }
    
    fun getIncomeStatementsBySymbolAndDateRangeAndType(
        symbol: String,
        isQuarterly: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<IncomeStatementDto> {
        return incomeStatementRepository.findBySymbolAndIsQuarterlyAndFiscalDateEndingBetweenOrderByFiscalDateEndingDesc(
            symbol, isQuarterly, startDate, endDate, pageable
        ).map { IncomeStatementDto.from(it) }
    }
    
    fun getIncomeStatementsBySymbolAndYear(
        symbol: String,
        year: Int,
        pageable: Pageable
    ): Page<IncomeStatementDto> {
        return incomeStatementRepository.findBySymbolAndYearOrderByFiscalDateEndingDesc(
            symbol, year, pageable
        ).map { IncomeStatementDto.from(it) }
    }
    
    fun getIncomeStatementsBySymbolAndYearAndType(
        symbol: String,
        year: Int,
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<IncomeStatementDto> {
        return incomeStatementRepository.findBySymbolAndYearAndIsQuarterlyOrderByFiscalDateEndingDesc(
            symbol, year, isQuarterly, pageable
        ).map { IncomeStatementDto.from(it) }
    }
    
    fun getIncomeStatementsBySymbolAndQuarter(
        symbol: String,
        year: Int,
        quarter: Int,
        pageable: Pageable
    ): Page<IncomeStatementDto> {
        return incomeStatementRepository.findBySymbolAndYearAndQuarterOrderByFiscalDateEndingDesc(
            symbol, year, quarter, pageable
        ).map { IncomeStatementDto.from(it) }
    }
    
    fun getLatestIncomeStatement(symbol: String, isQuarterly: Boolean): IncomeStatementDto {
        val incomeStatement = incomeStatementRepository.findTopBySymbolAndIsQuarterlyOrderByFiscalDateEndingDesc(
            symbol, isQuarterly
        ) ?: throw IncomeStatementNotFoundException(
            "No income statement found for symbol: $symbol, quarterly: $isQuarterly"
        )
        return IncomeStatementDto.from(incomeStatement)
    }
    
    fun getIncomeStatementsBySymbolAndMinRevenue(
        symbol: String,
        minRevenue: Long,
        pageable: Pageable
    ): Page<IncomeStatementDto> {
        return incomeStatementRepository.findBySymbolAndTotalRevenueGreaterThanOrderByFiscalDateEndingDesc(
            symbol, minRevenue, pageable
        ).map { IncomeStatementDto.from(it) }
    }
    
    fun getIncomeStatementsBySymbolAndMinNetIncome(
        symbol: String,
        minNetIncome: Long,
        pageable: Pageable
    ): Page<IncomeStatementDto> {
        return incomeStatementRepository.findBySymbolAndNetIncomeGreaterThanOrderByFiscalDateEndingDesc(
            symbol, minNetIncome, pageable
        ).map { IncomeStatementDto.from(it) }
    }
    
    fun getIncomeStatementsByYear(year: Int, pageable: Pageable): Page<IncomeStatementDto> {
        return incomeStatementRepository.findByYearOrderByFiscalDateEndingDesc(year, pageable)
            .map { IncomeStatementDto.from(it) }
    }
    
    fun getIncomeStatementsByYearAndType(
        year: Int,
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<IncomeStatementDto> {
        return incomeStatementRepository.findByYearAndIsQuarterlyOrderByFiscalDateEndingDesc(
            year, isQuarterly, pageable
        ).map { IncomeStatementDto.from(it) }
    }
}
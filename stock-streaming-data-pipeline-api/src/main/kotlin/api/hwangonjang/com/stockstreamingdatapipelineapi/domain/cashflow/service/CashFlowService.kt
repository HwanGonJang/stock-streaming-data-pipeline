package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.service

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.dto.CashFlowDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.exception.CashFlowNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.repository.CashFlowRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CashFlowService(
    private val cashFlowRepository: CashFlowRepository
) {
    
    fun getCashFlowsBySymbol(symbol: String, pageable: Pageable): Page<CashFlowDto> {
        return cashFlowRepository.findBySymbolOrderByFiscalDateEndingDesc(symbol, pageable)
            .map { CashFlowDto.from(it) }
    }
    
    fun getCashFlowsBySymbolAndType(
        symbol: String,
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<CashFlowDto> {
        return cashFlowRepository.findBySymbolAndIsQuarterlyOrderByFiscalDateEndingDesc(
            symbol, isQuarterly, pageable
        ).map { CashFlowDto.from(it) }
    }
    
    fun getCashFlowsBySymbolAndDateRange(
        symbol: String,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<CashFlowDto> {
        return cashFlowRepository.findBySymbolAndFiscalDateEndingBetweenOrderByFiscalDateEndingDesc(
            symbol, startDate, endDate, pageable
        ).map { CashFlowDto.from(it) }
    }
    
    fun getCashFlowsBySymbolAndDateRangeAndType(
        symbol: String,
        isQuarterly: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<CashFlowDto> {
        return cashFlowRepository.findBySymbolAndIsQuarterlyAndFiscalDateEndingBetweenOrderByFiscalDateEndingDesc(
            symbol, isQuarterly, startDate, endDate, pageable
        ).map { CashFlowDto.from(it) }
    }
    
    fun getCashFlowsBySymbolAndYear(
        symbol: String,
        year: Int,
        pageable: Pageable
    ): Page<CashFlowDto> {
        return cashFlowRepository.findBySymbolAndYearOrderByFiscalDateEndingDesc(
            symbol, year, pageable
        ).map { CashFlowDto.from(it) }
    }
    
    fun getCashFlowsBySymbolAndYearAndType(
        symbol: String,
        year: Int,
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<CashFlowDto> {
        return cashFlowRepository.findBySymbolAndYearAndIsQuarterlyOrderByFiscalDateEndingDesc(
            symbol, year, isQuarterly, pageable
        ).map { CashFlowDto.from(it) }
    }
    
    fun getCashFlowsBySymbolAndQuarter(
        symbol: String,
        year: Int,
        quarter: Int,
        pageable: Pageable
    ): Page<CashFlowDto> {
        return cashFlowRepository.findBySymbolAndYearAndQuarterOrderByFiscalDateEndingDesc(
            symbol, year, quarter, pageable
        ).map { CashFlowDto.from(it) }
    }
    
    fun getLatestCashFlow(symbol: String, isQuarterly: Boolean): CashFlowDto {
        val cashFlow = cashFlowRepository.findTopBySymbolAndIsQuarterlyOrderByFiscalDateEndingDesc(
            symbol, isQuarterly
        ) ?: throw CashFlowNotFoundException(
            "No income statement found for symbol: $symbol, quarterly: $isQuarterly"
        )
        return CashFlowDto.from(cashFlow)
    }
    
    fun getCashFlowsBySymbolAndMinNetIncome(
        symbol: String,
        minNetIncome: Long,
        pageable: Pageable
    ): Page<CashFlowDto> {
        return cashFlowRepository.findBySymbolAndNetIncomeGreaterThanOrderByFiscalDateEndingDesc(
            symbol, minNetIncome, pageable
        ).map { CashFlowDto.from(it) }
    }
    
    fun getCashFlowsByYear(year: Int, pageable: Pageable): Page<CashFlowDto> {
        return cashFlowRepository.findByYearOrderByFiscalDateEndingDesc(year, pageable)
            .map { CashFlowDto.from(it) }
    }
    
    fun getCashFlowsByYearAndType(
        year: Int,
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<CashFlowDto> {
        return cashFlowRepository.findByYearAndIsQuarterlyOrderByFiscalDateEndingDesc(
            year, isQuarterly, pageable
        ).map { CashFlowDto.from(it) }
    }
}
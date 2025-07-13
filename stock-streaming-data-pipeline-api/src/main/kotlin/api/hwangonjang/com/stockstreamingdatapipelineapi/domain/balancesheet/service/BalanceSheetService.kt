package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.service

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.dto.BalanceSheetDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.exception.BalanceSheetNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.repository.BalanceSheetRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BalanceSheetService(
    private val balanceSheetRepository: BalanceSheetRepository
) {
    
    fun getBalanceSheetsBySymbol(symbol: String, pageable: Pageable): Page<BalanceSheetDto> {
        return balanceSheetRepository.findBySymbolOrderByFiscalDateEndingDesc(symbol, pageable)
            .map { BalanceSheetDto.from(it) }
    }
    
    fun getBalanceSheetsBySymbolAndType(
        symbol: String,
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<BalanceSheetDto> {
        return balanceSheetRepository.findBySymbolAndIsQuarterlyOrderByFiscalDateEndingDesc(
            symbol, isQuarterly, pageable
        ).map { BalanceSheetDto.from(it) }
    }
    
    fun getBalanceSheetsBySymbolAndDateRange(
        symbol: String,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<BalanceSheetDto> {
        return balanceSheetRepository.findBySymbolAndFiscalDateEndingBetweenOrderByFiscalDateEndingDesc(
            symbol, startDate, endDate, pageable
        ).map { BalanceSheetDto.from(it) }
    }
    
    fun getBalanceSheetsBySymbolAndDateRangeAndType(
        symbol: String,
        isQuarterly: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<BalanceSheetDto> {
        return balanceSheetRepository.findBySymbolAndIsQuarterlyAndFiscalDateEndingBetweenOrderByFiscalDateEndingDesc(
            symbol, isQuarterly, startDate, endDate, pageable
        ).map { BalanceSheetDto.from(it) }
    }
    
    fun getBalanceSheetsBySymbolAndYear(
        symbol: String,
        year: Int,
        pageable: Pageable
    ): Page<BalanceSheetDto> {
        return balanceSheetRepository.findBySymbolAndYearOrderByFiscalDateEndingDesc(
            symbol, year, pageable
        ).map { BalanceSheetDto.from(it) }
    }
    
    fun getBalanceSheetsBySymbolAndYearAndType(
        symbol: String,
        year: Int,
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<BalanceSheetDto> {
        return balanceSheetRepository.findBySymbolAndYearAndIsQuarterlyOrderByFiscalDateEndingDesc(
            symbol, year, isQuarterly, pageable
        ).map { BalanceSheetDto.from(it) }
    }
    
    fun getBalanceSheetsBySymbolAndQuarter(
        symbol: String,
        year: Int,
        quarter: Int,
        pageable: Pageable
    ): Page<BalanceSheetDto> {
        return balanceSheetRepository.findBySymbolAndYearAndQuarterOrderByFiscalDateEndingDesc(
            symbol, year, quarter, pageable
        ).map { BalanceSheetDto.from(it) }
    }
    
    fun getLatestBalanceSheet(symbol: String, isQuarterly: Boolean): BalanceSheetDto {
        val balanceSheet = balanceSheetRepository.findTopBySymbolAndIsQuarterlyOrderByFiscalDateEndingDesc(
            symbol, isQuarterly
        ) ?: throw BalanceSheetNotFoundException(
            "No income statement found for symbol: $symbol, quarterly: $isQuarterly"
        )
        return BalanceSheetDto.from(balanceSheet)
    }
    
    fun getBalanceSheetsByYear(year: Int, pageable: Pageable): Page<BalanceSheetDto> {
        return balanceSheetRepository.findByYearOrderByFiscalDateEndingDesc(year, pageable)
            .map { BalanceSheetDto.from(it) }
    }
    
    fun getBalanceSheetsByYearAndType(
        year: Int,
        isQuarterly: Boolean,
        pageable: Pageable
    ): Page<BalanceSheetDto> {
        return balanceSheetRepository.findByYearAndIsQuarterlyOrderByFiscalDateEndingDesc(
            year, isQuarterly, pageable
        ).map { BalanceSheetDto.from(it) }
    }
}
package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.service

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.dto.DailyPriceDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.exception.DailyPriceNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.repository.DailyPriceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DailyPriceService(
    private val dailyPriceRepository: DailyPriceRepository
) {
    
    fun getDailyPricesBySymbol(symbol: String, pageable: Pageable): Page<DailyPriceDto> {
        return dailyPriceRepository.findBySymbolOrderByDateDesc(symbol, pageable)
            .map { DailyPriceDto.from(it) }
    }
    
    fun getDailyPricesBySymbolAndDateRange(
        symbol: String, 
        startDate: LocalDate, 
        endDate: LocalDate, 
        sortOrder: String = "desc",
        pageable: Pageable
    ): Page<DailyPriceDto> {
        return if (sortOrder.lowercase() == "asc") {
            dailyPriceRepository.findBySymbolAndDateBetweenOrderByDateAsc(symbol, startDate, endDate, pageable)
        } else {
            dailyPriceRepository.findBySymbolAndDateBetweenOrderByDateDesc(symbol, startDate, endDate, pageable)
        }.map { DailyPriceDto.from(it) }
    }
    
    fun getDailyPricesByDateRange(
        startDate: LocalDate, 
        endDate: LocalDate, 
        pageable: Pageable
    ): Page<DailyPriceDto> {
        return dailyPriceRepository.findByDateBetweenOrderByDateDesc(startDate, endDate, pageable)
            .map { DailyPriceDto.from(it) }
    }
    
    fun getDailyPricesByYear(year: Int, pageable: Pageable): Page<DailyPriceDto> {
        return dailyPriceRepository.findByYearOrderByDateDesc(year, pageable)
            .map { DailyPriceDto.from(it) }
    }
    
    fun getDailyPricesBySymbolAndYear(symbol: String, year: Int, pageable: Pageable): Page<DailyPriceDto> {
        return dailyPriceRepository.findBySymbolAndYearOrderByDateDesc(symbol, year, pageable)
            .map { DailyPriceDto.from(it) }
    }
    
    fun getDailyPricesBySymbolAndQuarter(
        symbol: String, 
        year: Int, 
        quarter: Int, 
        pageable: Pageable
    ): Page<DailyPriceDto> {
        return dailyPriceRepository.findBySymbolAndYearAndQuarterOrderByDateDesc(symbol, year, quarter, pageable)
            .map { DailyPriceDto.from(it) }
    }
    
    fun getDailyPricesBySymbolAndMonth(
        symbol: String, 
        year: Int, 
        month: Int, 
        pageable: Pageable
    ): Page<DailyPriceDto> {
        return dailyPriceRepository.findBySymbolAndYearAndMonthOrderByDateDesc(symbol, year, month, pageable)
            .map { DailyPriceDto.from(it) }
    }
    
    fun getLatestDailyPrice(symbol: String): DailyPriceDto {
        val dailyPrice = dailyPriceRepository.findTopBySymbolOrderByDateDesc(symbol)
            ?: throw DailyPriceNotFoundException("No daily price found for symbol: $symbol")
        return DailyPriceDto.from(dailyPrice)
    }
    
    fun getDailyPricesBySymbolAndMinVolume(
        symbol: String, 
        minVolume: Long, 
        pageable: Pageable
    ): Page<DailyPriceDto> {
        return dailyPriceRepository.findBySymbolAndVolumeGreaterThanOrderByDateDesc(symbol, minVolume, pageable)
            .map { DailyPriceDto.from(it) }
    }
}
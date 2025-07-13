package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.entity.DailyPrice
import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDate

data class DailyPriceDto(
    val id: Long?,
    val symbol: String,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate,
    
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long
) {
    companion object {
        fun from(entity: DailyPrice): DailyPriceDto {
            return DailyPriceDto(
                id = entity.id,
                symbol = entity.symbol,
                date = entity.date,
                open = entity.open,
                high = entity.high,
                low = entity.low,
                close = entity.close,
                volume = entity.volume
            )
        }
    }
}
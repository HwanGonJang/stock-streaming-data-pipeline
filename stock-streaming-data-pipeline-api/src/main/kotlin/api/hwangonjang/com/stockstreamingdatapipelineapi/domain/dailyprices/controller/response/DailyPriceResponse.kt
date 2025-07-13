package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.controller.response

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.dto.DailyPriceDto
import java.math.BigDecimal
import java.time.LocalDate

data class DailyPriceResponse(
    val id: Long?,
    val symbol: String,
    val date: LocalDate,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long
) {
    companion object {
        fun from(dto: DailyPriceDto): DailyPriceResponse {
            return DailyPriceResponse(
                id = dto.id,
                symbol = dto.symbol,
                date = dto.date,
                open = dto.open,
                high = dto.high,
                low = dto.low,
                close = dto.close,
                volume = dto.volume
            )
        }
    }
}
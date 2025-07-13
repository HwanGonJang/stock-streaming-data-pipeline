package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.controller.response

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.dto.TradeDto
import java.time.LocalDateTime
import java.util.*

data class TradeResponse(
    val uuid: UUID,
    val symbol: String,
    val tradeConditions: String,
    val price: Double,
    val volume: Double,
    val tradeTimestamp: LocalDateTime,
    val ingestTimestamp: LocalDateTime
) {
    companion object {
        fun from(tradeDto: TradeDto): TradeResponse {
            return TradeResponse(
                uuid = tradeDto.uuid,
                symbol = tradeDto.symbol,
                tradeConditions = tradeDto.tradeConditions,
                price = tradeDto.price,
                volume = tradeDto.volume,
                tradeTimestamp = tradeDto.tradeTimestamp,
                ingestTimestamp = tradeDto.ingestTimestamp
            )
        }
    }
}
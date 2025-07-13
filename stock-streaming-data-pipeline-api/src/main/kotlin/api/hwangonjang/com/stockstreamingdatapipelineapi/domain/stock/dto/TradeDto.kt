package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.entity.Trade
import java.time.LocalDateTime
import java.util.*

data class TradeDto(
    val uuid: UUID,
    val symbol: String,
    val tradeConditions: String,
    val price: Double,
    val volume: Double,
    val tradeTimestamp: LocalDateTime,
    val ingestTimestamp: LocalDateTime
) {
    companion object {
        fun from(trade: Trade): TradeDto {
            return TradeDto(
                uuid = trade.uuid,
                symbol = trade.primaryKey.symbol,
                tradeConditions = trade.tradeConditions,
                price = trade.price,
                volume = trade.volume,
                tradeTimestamp = trade.primaryKey.tradeTimestamp,
                ingestTimestamp = trade.ingestTimestamp
            )
        }
    }
}
package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.repository

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.entity.Trade
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.entity.TradePrimaryKey
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.repository.query.Param
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface TradeRepository : ReactiveCassandraRepository<Trade, TradePrimaryKey> {
    
    @Query("SELECT * FROM trades WHERE symbol = :symbol AND trade_timestamp >= :fromTime ORDER BY trade_timestamp DESC")
    fun findBySymbolAndTradeTimestampGreaterThanEqual(
        @Param("symbol") symbol: String,
        @Param("fromTime") fromTime: LocalDateTime,
    ): Flux<Trade>
    
    @Query("SELECT * FROM trades WHERE symbol = :symbol AND trade_timestamp >= :fromTime AND trade_timestamp <= :toTime ORDER BY trade_timestamp DESC LIMIT 10")
    fun findBySymbolAndTradeTimestampBetween(
        @Param("symbol") symbol: String,
        @Param("fromTime") fromTime: LocalDateTime,
        @Param("toTime") toTime: LocalDateTime
    ): Flux<Trade>
    
    @Query("SELECT * FROM trades WHERE symbol = :symbol ORDER BY trade_timestamp DESC LIMIT :limit")
    fun findLatestTradesBySymbol(
        @Param("symbol") symbol: String,
        @Param("limit") limit: Int
    ): Flux<Trade>
}
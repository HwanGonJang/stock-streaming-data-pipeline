package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.entity

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable
import java.time.LocalDateTime

@PrimaryKeyClass
data class TradePrimaryKey(
    @PrimaryKeyColumn(name = "symbol", type = PrimaryKeyType.PARTITIONED)
    val symbol: String,
    
    @PrimaryKeyColumn(name = "trade_timestamp", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val tradeTimestamp: LocalDateTime
) : Serializable
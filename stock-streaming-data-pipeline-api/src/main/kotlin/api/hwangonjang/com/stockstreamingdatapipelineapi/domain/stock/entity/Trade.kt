package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.entity

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("trades")
data class Trade(
    @PrimaryKey
    val primaryKey: TradePrimaryKey,
    
    @Column("uuid")
    val uuid: UUID,
    
    @Column("trade_conditions")
    val tradeConditions: String,
    
    @Column("price")
    val price: Double,
    
    @Column("volume")
    val volume: Double,
    
    @Column("ingest_timestamp")
    val ingestTimestamp: LocalDateTime
)
package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "stocks")
class StockInfo(
    @Id
    @Column(name = "symbol", nullable = false, unique = true, length = 10)
    val symbol: String,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "exchange", nullable = false, length = 20)
    val exchange: String,

    @Column(name = "asset_type", nullable = false, length = 20)
    val assetType: String,

    @Column(name = "ipo_date")
    val ipoDate: LocalDate? = null,

    @Column(name = "delisting_date")
    val delistingDate: LocalDate? = null,

    @Column(name = "status", nullable = false, length = 20)
    val status: String,

    @LastModifiedDate
    @Column(name = "last_updated", nullable = false, updatable = false)
    val lastUpdatedAt: LocalDateTime? = null
)
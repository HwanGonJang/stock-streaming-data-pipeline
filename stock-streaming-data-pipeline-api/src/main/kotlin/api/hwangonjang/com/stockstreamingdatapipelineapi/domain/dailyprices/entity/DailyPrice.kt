package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "daily_prices")
class DailyPrice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "symbol", nullable = false, unique = true, length = 10)
    val symbol: String,

    @Column(name = "date", nullable = false)
    val date: LocalDate,

    @Column(name = "open", nullable = false, precision = 18, scale = 4)
    val open: BigDecimal,

    @Column(name = "high", nullable = false, precision = 18, scale = 4)
    val high: BigDecimal,

    @Column(name = "low", nullable = false, precision = 18, scale = 4)
    val low: BigDecimal,

    @Column(name = "close", nullable = false, precision = 18, scale = 4)
    val close: BigDecimal,

    @Column(name = "volume", nullable = false)
    val volume: Long
)
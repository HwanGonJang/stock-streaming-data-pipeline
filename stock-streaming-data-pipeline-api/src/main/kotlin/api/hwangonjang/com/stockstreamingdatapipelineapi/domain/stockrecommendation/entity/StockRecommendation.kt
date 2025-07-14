package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.entity

import jakarta.persistence.*
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "stock_recommendations")
class StockRecommendation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "symbol", length = 10)
    val symbol: String,

    @Column(name = "recommendation_score", nullable = false, precision = 5, scale = 4)
    val recommendationScore: BigDecimal,

    @Column(name = "recommendation_label", nullable = false, length = 20)
    val recommendationLabel: String,

    @Column(name = "summary", columnDefinition = "TEXT")
    val summary: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "last_updated", nullable = false)
    val lastUpdatedAt: LocalDateTime? = null
)
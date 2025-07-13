package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "news_stocks")
class NewsStock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    val newsArticle: NewsArticle,

    @Column(name = "symbol", nullable = false, length = 10)
    val symbol: String,

    @Column(name = "relevance_score", precision = 5, scale = 4)
    val relevanceScore: BigDecimal? = null,

    @Column(name = "sentiment_score", precision = 5, scale = 4)
    val sentimentScore: BigDecimal? = null,

    @Column(name = "sentiment_label", length = 20)
    val sentimentLabel: String? = null
)
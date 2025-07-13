package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.entity

import jakarta.persistence.*
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "news_articles")
class NewsArticle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title", nullable = false, length = 255)
    val title: String,

    @Column(name = "url", nullable = false, length = 500, unique = true)
    val url: String,

    @Column(name = "time_published", nullable = false)
    val timePublished: LocalDateTime,

    @Column(name = "authors", columnDefinition = "TEXT[]")
    val authors: Array<String>? = null,

    @Column(name = "summary", columnDefinition = "TEXT")
    val summary: String? = null,

    @Column(name = "source", length = 100)
    val source: String? = null,

    @Column(name = "category_within_source", length = 100)
    val categoryWithinSource: String? = null,

    @Column(name = "source_domain", length = 100)
    val sourceDomain: String? = null,

    @Column(name = "overall_sentiment_score", precision = 5, scale = 4)
    val overallSentimentScore: BigDecimal? = null,

    @Column(name = "overall_sentiment_label", length = 20)
    val overallSentimentLabel: String? = null,

    @LastModifiedDate
    @Column(name = "last_updated")
    val lastUpdated: LocalDateTime? = null,

    @OneToMany(mappedBy = "newsArticle", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val newsStocks: List<NewsStock> = mutableListOf()
)
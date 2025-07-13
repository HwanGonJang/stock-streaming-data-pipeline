package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.controller.request

import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDateTime

data class NewsRequest(
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val startDate: LocalDateTime? = null,
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val endDate: LocalDateTime? = null,
    
    val source: String? = null,
    val category: String? = null,
    val sourceDomain: String? = null,
    val sentimentLabel: String? = null,
    val minSentimentScore: BigDecimal? = null,
    val maxSentimentScore: BigDecimal? = null,
    val minRelevanceScore: BigDecimal? = null,
    val keyword: String? = null
)
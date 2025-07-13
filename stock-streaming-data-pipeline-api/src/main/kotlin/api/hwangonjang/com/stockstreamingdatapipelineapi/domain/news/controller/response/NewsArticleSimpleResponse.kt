package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.controller.response

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.dto.NewsArticleDto
import java.math.BigDecimal
import java.time.LocalDateTime

data class NewsArticleSimpleResponse(
    val id: Long?,
    val title: String,
    val url: String,
    val timePublished: LocalDateTime,
    val authors: Array<String>?,
    val summary: String?,
    val source: String?,
    val categoryWithinSource: String?,
    val sourceDomain: String?,
    val overallSentimentScore: BigDecimal?,
    val overallSentimentLabel: String?,
    val lastUpdated: LocalDateTime?
) {
    companion object {
        fun from(dto: NewsArticleDto): NewsArticleSimpleResponse {
            return NewsArticleSimpleResponse(
                id = dto.id,
                title = dto.title,
                url = dto.url,
                timePublished = dto.timePublished,
                authors = dto.authors,
                summary = dto.summary,
                source = dto.source,
                categoryWithinSource = dto.categoryWithinSource,
                sourceDomain = dto.sourceDomain,
                overallSentimentScore = dto.overallSentimentScore,
                overallSentimentLabel = dto.overallSentimentLabel,
                lastUpdated = dto.lastUpdated
            )
        }
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NewsArticleSimpleResponse) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
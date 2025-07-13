package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.entity.NewsArticle
import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

data class NewsArticleSimpleDto(
    val id: Long?,
    val title: String,
    val url: String,
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timePublished: LocalDateTime,
    
    val authors: Array<String>?,
    val summary: String?,
    val source: String?,
    val categoryWithinSource: String?,
    val sourceDomain: String?,
    val overallSentimentScore: BigDecimal?,
    val overallSentimentLabel: String?,
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    val lastUpdated: LocalDateTime?,
) {
    companion object {
        fun from(entity: NewsArticle): NewsArticleSimpleDto {
            return NewsArticleSimpleDto(
                id = entity.id,
                title = entity.title,
                url = entity.url,
                timePublished = entity.timePublished,
                authors = entity.authors,
                summary = entity.summary,
                source = entity.source,
                categoryWithinSource = entity.categoryWithinSource,
                sourceDomain = entity.sourceDomain,
                overallSentimentScore = entity.overallSentimentScore,
                overallSentimentLabel = entity.overallSentimentLabel,
                lastUpdated = entity.lastUpdated
            )
        }
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NewsArticleSimpleDto) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.entity.NewsStock
import java.math.BigDecimal

data class NewsStockDto(
    val id: Long?,
    val newsArticle: NewsArticleDto,
    val symbol: String,
    val relevanceScore: BigDecimal?,
    val sentimentScore: BigDecimal?,
    val sentimentLabel: String?
) {
    companion object {
        fun from(entity: NewsStock): NewsStockDto {
            return NewsStockDto(
                id = entity.id,
                newsArticle = NewsArticleDto.from(entity.newsArticle),
                symbol = entity.symbol,
                relevanceScore = entity.relevanceScore,
                sentimentScore = entity.sentimentScore,
                sentimentLabel = entity.sentimentLabel
            )
        }
    }
}
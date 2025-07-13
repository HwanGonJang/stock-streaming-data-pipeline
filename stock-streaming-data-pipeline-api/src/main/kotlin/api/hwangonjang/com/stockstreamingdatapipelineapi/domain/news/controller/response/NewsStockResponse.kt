package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.controller.response

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.dto.NewsStockDto
import java.math.BigDecimal

data class NewsStockResponse(
    val id: Long?,
    val newsArticle: NewsArticleSimpleResponse,
    val symbol: String,
    val relevanceScore: BigDecimal?,
    val sentimentScore: BigDecimal?,
    val sentimentLabel: String?
) {
    companion object {
        fun from(dto: NewsStockDto): NewsStockResponse {
            return NewsStockResponse(
                id = dto.id,
                newsArticle = NewsArticleSimpleResponse.from(dto.newsArticle),
                symbol = dto.symbol,
                relevanceScore = dto.relevanceScore,
                sentimentScore = dto.sentimentScore,
                sentimentLabel = dto.sentimentLabel
            )
        }
    }
}
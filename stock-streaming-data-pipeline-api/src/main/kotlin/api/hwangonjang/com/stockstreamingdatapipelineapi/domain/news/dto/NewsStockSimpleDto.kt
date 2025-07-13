package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.entity.NewsStock
import java.math.BigDecimal

data class NewsStockSimpleDto(
    val id: Long?,
    val symbol: String,
    val relevanceScore: BigDecimal?,
    val sentimentScore: BigDecimal?,
    val sentimentLabel: String?
) {
    companion object {
        fun from(entity: NewsStock): NewsStockSimpleDto {
            return NewsStockSimpleDto(
                id = entity.id,
                symbol = entity.symbol,
                relevanceScore = entity.relevanceScore,
                sentimentScore = entity.sentimentScore,
                sentimentLabel = entity.sentimentLabel
            )
        }
    }
}
package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.controller.response

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.dto.NewsStockSimpleDto
import java.math.BigDecimal

data class NewsStockSimpleResponse(
    val id: Long?,
    val symbol: String,
    val relevanceScore: BigDecimal?,
    val sentimentScore: BigDecimal?,
    val sentimentLabel: String?
) {
    companion object {
        fun from(dto: NewsStockSimpleDto): NewsStockSimpleResponse {
            return NewsStockSimpleResponse(
                id = dto.id,
                symbol = dto.symbol,
                relevanceScore = dto.relevanceScore,
                sentimentScore = dto.sentimentScore,
                sentimentLabel = dto.sentimentLabel
            )
        }
    }
}
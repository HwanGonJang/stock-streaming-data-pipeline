package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.entity.StockRecommendation
import java.math.BigDecimal
import java.time.LocalDateTime

data class StockRecommendationDto(
    val id: Int,
    val symbol: String,
    val recommendationScore: BigDecimal,
    val recommendationLabel: String,
    val summary: String?,
    val createdAt: LocalDateTime?,
    val lastUpdatedAt: LocalDateTime?
) {
    companion object {
        fun from(stockRecommendation: StockRecommendation): StockRecommendationDto {
            return StockRecommendationDto(
                id = stockRecommendation.id,
                symbol = stockRecommendation.symbol,
                recommendationScore = stockRecommendation.recommendationScore,
                recommendationLabel = stockRecommendation.recommendationLabel,
                summary = stockRecommendation.summary,
                createdAt = stockRecommendation.createdAt,
                lastUpdatedAt = stockRecommendation.lastUpdatedAt
            )
        }
    }
}
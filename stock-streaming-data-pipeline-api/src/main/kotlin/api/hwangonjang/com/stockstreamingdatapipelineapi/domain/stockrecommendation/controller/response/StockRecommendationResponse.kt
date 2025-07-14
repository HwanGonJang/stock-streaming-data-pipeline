package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.controller.response

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.dto.StockRecommendationDto
import java.math.BigDecimal
import java.time.LocalDateTime

data class StockRecommendationResponse(
    val id: Int,
    val symbol: String,
    val recommendationScore: BigDecimal,
    val recommendationLabel: String,
    val summary: String?,
    val createdAt: LocalDateTime?,
    val lastUpdatedAt: LocalDateTime?
) {
    companion object {
        fun from(stockRecommendationDto: StockRecommendationDto): StockRecommendationResponse {
            return StockRecommendationResponse(
                id = stockRecommendationDto.id,
                symbol = stockRecommendationDto.symbol,
                recommendationScore = stockRecommendationDto.recommendationScore,
                recommendationLabel = stockRecommendationDto.recommendationLabel,
                summary = stockRecommendationDto.summary,
                createdAt = stockRecommendationDto.createdAt,
                lastUpdatedAt = stockRecommendationDto.lastUpdatedAt
            )
        }
    }
}
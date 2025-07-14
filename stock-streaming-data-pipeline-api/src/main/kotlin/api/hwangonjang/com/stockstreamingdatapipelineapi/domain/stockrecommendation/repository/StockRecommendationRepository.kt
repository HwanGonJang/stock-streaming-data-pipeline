package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.repository

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.entity.StockRecommendation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRecommendationRepository : JpaRepository<StockRecommendation, Int> {
    fun findBySymbol(symbol: String): StockRecommendation?
    fun findAllByOrderByRecommendationScoreDesc(): List<StockRecommendation>
}
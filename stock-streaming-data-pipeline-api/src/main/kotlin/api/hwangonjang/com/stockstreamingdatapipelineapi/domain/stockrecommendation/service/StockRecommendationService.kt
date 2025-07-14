package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.service

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.dto.StockRecommendationDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.exception.StockRecommendationNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.repository.StockRecommendationRepository
import org.springframework.stereotype.Service

@Service
class StockRecommendationService(
    private val stockRecommendationRepository: StockRecommendationRepository,
) {
    fun getStockRecommendationBySymbol(symbol: String): StockRecommendationDto {
        val stockRecommendation = stockRecommendationRepository.findBySymbol(symbol) 
            ?: throw StockRecommendationNotFoundException("Stock recommendation for $symbol not found")
        
        return StockRecommendationDto.from(stockRecommendation)
    }
    
    fun getAllStockRecommendations(): List<StockRecommendationDto> {
        return stockRecommendationRepository.findAllByOrderByRecommendationScoreDesc()
            .map { StockRecommendationDto.from(it) }
    }
}
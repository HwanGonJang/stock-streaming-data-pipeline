package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.controller

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.controller.response.StockRecommendationResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.service.StockRecommendationService
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.enums.StockSymbol
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.logging.TraceIdResolver
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.response.StockApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "📊 Stock Recommendation API", description = "주식 추천 관련 API")
@RequestMapping("/v1/stocks/recommendations")
class StockRecommendationController(
    private val traceIdResolver: TraceIdResolver,
    private val stockRecommendationService: StockRecommendationService,
) {
    @Operation(
        summary = "Get stock recommendation by symbol",
        description = "Get stock recommendation for a specific symbol"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Stock recommendation not found")
        ]
    )
    @GetMapping("/{symbol}")
    fun getStockRecommendationBySymbol(
        @PathVariable symbol: StockSymbol,
    ): StockApiResponse<StockRecommendationResponse> {
        val stockRecommendation = stockRecommendationService.getStockRecommendationBySymbol(symbol.symbol)

        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = StockRecommendationResponse.from(stockRecommendation)
        )
    }

    @Operation(
        summary = "Get all stock recommendations",
        description = "Get all stock recommendations ordered by recommendation score (descending)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success")
        ]
    )
    @GetMapping
    fun getAllStockRecommendations(): StockApiResponse<List<StockRecommendationResponse>> {
        val stockRecommendations = stockRecommendationService.getAllStockRecommendations()
            .map { StockRecommendationResponse.from(it) }

        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = stockRecommendations
        )
    }
}
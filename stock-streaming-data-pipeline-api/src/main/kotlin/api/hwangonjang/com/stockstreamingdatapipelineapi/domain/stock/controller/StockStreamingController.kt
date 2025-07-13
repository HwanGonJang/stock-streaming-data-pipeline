package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.controller

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.controller.response.TradeResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.service.StockStreamingService
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.enums.StockSymbol
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@Tag(name = "📊 Stock Streaming API", description = "실시간 주식 데이터 스트리밍 API")
@RequestMapping("/v1/stocks/stream")
class StockStreamingController(
    private val stockStreamingService: StockStreamingService
) {
    
    @Operation(
        summary = "Stream real-time stock trade data",
        description = "클라이언트가 지정한 간격으로 실시간 주식 거래 데이터를 스트리밍합니다. 한국 시간 시뮬레이션 옵션을 통해 미국 주식 장이 한국 시간 오전 9시부터 열리는 것처럼 보이게 할 수 있습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success - SSE stream established"),
            ApiResponse(responseCode = "400", description = "Bad Request - Invalid parameters"),
            ApiResponse(responseCode = "404", description = "Stock symbol not found")
        ]
    )
    @GetMapping(value = ["/{symbol}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamTradeData(
        @Parameter(description = "주식 심볼 (예: AAPL, GOOGL, MSFT)")
        @PathVariable symbol: StockSymbol,
        
        @Parameter(description = "스트리밍 간격 (초 단위, 기본값: 1초)")
        @RequestParam(defaultValue = "1") intervalSeconds: Long,
        
        @Parameter(description = "한국 시간 시뮬레이션 사용 여부 (기본값: false)")
        @RequestParam(defaultValue = "false") useKoreanTimeSimulation: Boolean
    ): Flux<TradeResponse> {
        return stockStreamingService.streamRealTimeTradeData(
            symbol = symbol.symbol,
            intervalSeconds = intervalSeconds,
            useKoreanTimeSimulation = useKoreanTimeSimulation
        ).map { tradeDto -> TradeResponse.from(tradeDto) }
    }


    @Operation(
        summary = "Get latest trades",
        description = "특정 주식의 최신 거래 데이터를 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request"),
            ApiResponse(responseCode = "404", description = "Stock symbol not found")
        ]
    )
    @GetMapping("/{symbol}/latest")
    fun getLatestTrades(
        @Parameter(description = "주식 심볼 (예: AAPL, GOOGL, MSFT)")
        @PathVariable symbol: StockSymbol,
        
        @Parameter(description = "조회할 거래 건수 (기본값: 10)")
        @RequestParam(defaultValue = "10") limit: Int
    ): Flux<TradeResponse> {
        return stockStreamingService.getLatestTrades(symbol.symbol, limit)
            .map { tradeDto -> TradeResponse.from(tradeDto) }
    }
}
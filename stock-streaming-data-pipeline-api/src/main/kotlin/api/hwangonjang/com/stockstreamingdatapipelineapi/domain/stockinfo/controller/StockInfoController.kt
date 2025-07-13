package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.controller

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.controller.response.StockInfoResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.service.StockInfoService
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
@Tag(name = "📈 Stock API", description = "주식 관련 API")
@RequestMapping("/v1/stocks/info")
class StockInfoController(
    private val traceIdResolver: TraceIdResolver,
    private val stockInfoService: StockInfoService,
) {
    @Operation(
        summary = "Get stock info",
        description = "Get specific stock info"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}")
    fun getStockInfo(
        @PathVariable symbol: StockSymbol,
    ): StockApiResponse<StockInfoResponse> {
        val stockInfo =  stockInfoService.getStockInfoBySymbol(symbol.symbol)

        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = StockInfoResponse.from(stockInfo)
        )
    }
}
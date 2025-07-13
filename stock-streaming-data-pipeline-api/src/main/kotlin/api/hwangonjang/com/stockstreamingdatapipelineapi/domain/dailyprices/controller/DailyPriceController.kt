package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.controller

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.controller.request.DailyPriceRequest
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.controller.response.DailyPriceResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.service.DailyPriceService
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.enums.StockSymbol
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.logging.TraceIdResolver
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.response.StockApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "📊 Daily Price API", description = "일별 주가 데이터 API")
@RequestMapping("/v1/daily-prices")
class DailyPriceController(
    private val traceIdResolver: TraceIdResolver,
    private val dailyPriceService: DailyPriceService
) {
    
    @Operation(
        summary = "Get daily prices by symbol",
        description = "특정 종목의 일별 주가 데이터 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}")
    fun getDailyPricesBySymbol(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: DailyPriceRequest,
        @PageableDefault(size = 100) pageable: Pageable
    ): StockApiResponse<Page<DailyPriceResponse>> {
        val dailyPrices = when {
            request.startDate != null && request.endDate != null -> {
                dailyPriceService.getDailyPricesBySymbolAndDateRange(
                    symbol.symbol, request.startDate, request.endDate, request.sortOrder, pageable
                )
            }
            request.year != null && request.quarter != null -> {
                dailyPriceService.getDailyPricesBySymbolAndQuarter(
                    symbol.symbol, request.year, request.quarter, pageable
                )
            }
            request.year != null && request.month != null -> {
                dailyPriceService.getDailyPricesBySymbolAndMonth(
                    symbol.symbol, request.year, request.month, pageable
                )
            }
            request.year != null -> {
                dailyPriceService.getDailyPricesBySymbolAndYear(symbol.symbol, request.year, pageable)
            }
            request.minVolume != null -> {
                dailyPriceService.getDailyPricesBySymbolAndMinVolume(
                    symbol.symbol, request.minVolume, pageable
                )
            }
            else -> {
                dailyPriceService.getDailyPricesBySymbol(symbol.symbol, pageable)
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = dailyPrices.map { DailyPriceResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get latest daily price",
        description = "특정 종목의 최신 일별 주가 데이터 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Not Found")
        ]
    )
    @GetMapping("/{symbol}/latest")
    fun getLatestDailyPrice(
        @PathVariable symbol: StockSymbol
    ): StockApiResponse<DailyPriceResponse> {
        val latestPrice = dailyPriceService.getLatestDailyPrice(symbol.symbol)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = DailyPriceResponse.from(latestPrice)
        )
    }
    
    @Operation(
        summary = "Get daily prices by date range",
        description = "기간별 모든 종목의 일별 주가 데이터 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/range")
    fun getDailyPricesByDateRange(
        @ModelAttribute request: DailyPriceRequest,
        @PageableDefault(size = 100) pageable: Pageable
    ): StockApiResponse<Page<DailyPriceResponse>> {
        val dailyPrices = when {
            request.startDate != null && request.endDate != null -> {
                dailyPriceService.getDailyPricesByDateRange(
                    request.startDate, request.endDate, pageable
                )
            }
            request.year != null -> {
                dailyPriceService.getDailyPricesByYear(request.year, pageable)
            }
            else -> {
                throw IllegalArgumentException("Either date range or year must be specified")
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = dailyPrices.map { DailyPriceResponse.from(it) }
        )
    }
}
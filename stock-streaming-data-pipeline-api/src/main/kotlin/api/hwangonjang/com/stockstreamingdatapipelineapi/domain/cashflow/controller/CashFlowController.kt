package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.controller

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.controller.request.CashFlowRequest
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.controller.response.CashFlowResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.service.CashFlowService
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
@Tag(name = "💰 Cash Flow API", description = "현금흐름표 API")
@RequestMapping("/v1/cash-flow")
class CashFlowController(
    private val traceIdResolver: TraceIdResolver,
    private val cashFlowService: CashFlowService
) {
    
    @Operation(
        summary = "Get income statements by symbol",
        description = "특정 종목의 현금흐름표 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}")
    fun getCashFlowsBySymbol(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: CashFlowRequest,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<CashFlowResponse>> {
        val incomeStatements = when {
            request.startDate != null && request.endDate != null && request.isQuarterly != null -> {
                cashFlowService.getCashFlowsBySymbolAndDateRangeAndType(
                    symbol.symbol, request.isQuarterly, request.startDate, request.endDate, pageable
                )
            }
            request.startDate != null && request.endDate != null -> {
                cashFlowService.getCashFlowsBySymbolAndDateRange(
                    symbol.symbol, request.startDate, request.endDate, pageable
                )
            }
            request.year != null && request.quarter != null -> {
                cashFlowService.getCashFlowsBySymbolAndQuarter(
                    symbol.symbol, request.year, request.quarter, pageable
                )
            }
            request.year != null && request.isQuarterly != null -> {
                cashFlowService.getCashFlowsBySymbolAndYearAndType(
                    symbol.symbol, request.year, request.isQuarterly, pageable
                )
            }
            request.year != null -> {
                cashFlowService.getCashFlowsBySymbolAndYear(
                    symbol.symbol, request.year, pageable
                )
            }
            request.isQuarterly != null -> {
                cashFlowService.getCashFlowsBySymbolAndType(
                    symbol.symbol, request.isQuarterly, pageable
                )
            }
            request.minNetIncome != null -> {
                cashFlowService.getCashFlowsBySymbolAndMinNetIncome(
                    symbol.symbol, request.minNetIncome, pageable
                )
            }
            else -> {
                cashFlowService.getCashFlowsBySymbol(symbol.symbol, pageable)
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { CashFlowResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get latest income statement",
        description = "특정 종목의 최신 현금흐름표 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Not Found")
        ]
    )
    @GetMapping("/{symbol}/latest")
    fun getLatestCashFlow(
        @PathVariable symbol: StockSymbol,
        @RequestParam(defaultValue = "false") isQuarterly: Boolean
    ): StockApiResponse<CashFlowResponse> {
        val latestCashFlow = cashFlowService.getLatestCashFlow(symbol.symbol, isQuarterly)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = CashFlowResponse.from(latestCashFlow)
        )
    }
    
    @Operation(
        summary = "Get income statements by year",
        description = "연도별 모든 종목의 현금흐름표 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/year/{year}")
    fun getCashFlowsByYear(
        @PathVariable year: Int,
        @RequestParam(required = false) isQuarterly: Boolean?,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<CashFlowResponse>> {
        val incomeStatements = if (isQuarterly != null) {
            cashFlowService.getCashFlowsByYearAndType(year, isQuarterly, pageable)
        } else {
            cashFlowService.getCashFlowsByYear(year, pageable)
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { CashFlowResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get annual income statements",
        description = "연간 현금흐름표 조회 (isQuarterly=false)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}/annual")
    fun getAnnualCashFlows(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: CashFlowRequest,
        @PageableDefault(size = 20) pageable: Pageable
    ): StockApiResponse<Page<CashFlowResponse>> {
        val incomeStatements = when {
            request.startDate != null && request.endDate != null -> {
                cashFlowService.getCashFlowsBySymbolAndDateRangeAndType(
                    symbol.symbol, false, request.startDate, request.endDate, pageable
                )
            }
            request.year != null -> {
                cashFlowService.getCashFlowsBySymbolAndYearAndType(
                    symbol.symbol, request.year, false, pageable
                )
            }
            else -> {
                cashFlowService.getCashFlowsBySymbolAndType(
                    symbol.symbol, false, pageable
                )
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { CashFlowResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get quarterly income statements",
        description = "분기별 현금흐름표 조회 (isQuarterly=true)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}/quarterly")
    fun getQuarterlyCashFlows(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: CashFlowRequest,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<CashFlowResponse>> {
        val incomeStatements = when {
            request.startDate != null && request.endDate != null -> {
                cashFlowService.getCashFlowsBySymbolAndDateRangeAndType(
                    symbol.symbol, true, request.startDate, request.endDate, pageable
                )
            }
            request.year != null && request.quarter != null -> {
                cashFlowService.getCashFlowsBySymbolAndQuarter(
                    symbol.symbol, request.year, request.quarter, pageable
                )
            }
            request.year != null -> {
                cashFlowService.getCashFlowsBySymbolAndYearAndType(
                    symbol.symbol, request.year, true, pageable
                )
            }
            else -> {
                cashFlowService.getCashFlowsBySymbolAndType(
                    symbol.symbol, true, pageable
                )
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { CashFlowResponse.from(it) }
        )
    }
}
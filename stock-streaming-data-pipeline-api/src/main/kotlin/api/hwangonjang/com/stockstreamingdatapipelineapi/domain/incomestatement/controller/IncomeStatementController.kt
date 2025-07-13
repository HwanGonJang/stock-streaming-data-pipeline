package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.controller

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.controller.request.IncomeStatementRequest
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.controller.response.IncomeStatementResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.service.IncomeStatementService
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
@Tag(name = "💰 Income Statement API", description = "손익계산서 API")
@RequestMapping("/v1/income-statements")
class IncomeStatementController(
    private val traceIdResolver: TraceIdResolver,
    private val incomeStatementService: IncomeStatementService
) {
    
    @Operation(
        summary = "Get income statements by symbol",
        description = "특정 종목의 손익계산서 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}")
    fun getIncomeStatementsBySymbol(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: IncomeStatementRequest,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<IncomeStatementResponse>> {
        val incomeStatements = when {
            request.startDate != null && request.endDate != null && request.isQuarterly != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndDateRangeAndType(
                    symbol.symbol, request.isQuarterly, request.startDate, request.endDate, pageable
                )
            }
            request.startDate != null && request.endDate != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndDateRange(
                    symbol.symbol, request.startDate, request.endDate, pageable
                )
            }
            request.year != null && request.quarter != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndQuarter(
                    symbol.symbol, request.year, request.quarter, pageable
                )
            }
            request.year != null && request.isQuarterly != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndYearAndType(
                    symbol.symbol, request.year, request.isQuarterly, pageable
                )
            }
            request.year != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndYear(
                    symbol.symbol, request.year, pageable
                )
            }
            request.isQuarterly != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndType(
                    symbol.symbol, request.isQuarterly, pageable
                )
            }
            request.minRevenue != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndMinRevenue(
                    symbol.symbol, request.minRevenue, pageable
                )
            }
            request.minNetIncome != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndMinNetIncome(
                    symbol.symbol, request.minNetIncome, pageable
                )
            }
            else -> {
                incomeStatementService.getIncomeStatementsBySymbol(symbol.symbol, pageable)
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { IncomeStatementResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get latest income statement",
        description = "특정 종목의 최신 손익계산서 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Not Found")
        ]
    )
    @GetMapping("/{symbol}/latest")
    fun getLatestIncomeStatement(
        @PathVariable symbol: StockSymbol,
        @RequestParam(defaultValue = "false") isQuarterly: Boolean
    ): StockApiResponse<IncomeStatementResponse> {
        val latestIncomeStatement = incomeStatementService.getLatestIncomeStatement(symbol.symbol, isQuarterly)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = IncomeStatementResponse.from(latestIncomeStatement)
        )
    }
    
    @Operation(
        summary = "Get income statements by year",
        description = "연도별 모든 종목의 손익계산서 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/year/{year}")
    fun getIncomeStatementsByYear(
        @PathVariable year: Int,
        @RequestParam(required = false) isQuarterly: Boolean?,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<IncomeStatementResponse>> {
        val incomeStatements = if (isQuarterly != null) {
            incomeStatementService.getIncomeStatementsByYearAndType(year, isQuarterly, pageable)
        } else {
            incomeStatementService.getIncomeStatementsByYear(year, pageable)
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { IncomeStatementResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get annual income statements",
        description = "연간 손익계산서 조회 (isQuarterly=false)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}/annual")
    fun getAnnualIncomeStatements(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: IncomeStatementRequest,
        @PageableDefault(size = 20) pageable: Pageable
    ): StockApiResponse<Page<IncomeStatementResponse>> {
        val incomeStatements = when {
            request.startDate != null && request.endDate != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndDateRangeAndType(
                    symbol.symbol, false, request.startDate, request.endDate, pageable
                )
            }
            request.year != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndYearAndType(
                    symbol.symbol, request.year, false, pageable
                )
            }
            else -> {
                incomeStatementService.getIncomeStatementsBySymbolAndType(
                    symbol.symbol, false, pageable
                )
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { IncomeStatementResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get quarterly income statements",
        description = "분기별 손익계산서 조회 (isQuarterly=true)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}/quarterly")
    fun getQuarterlyIncomeStatements(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: IncomeStatementRequest,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<IncomeStatementResponse>> {
        val incomeStatements = when {
            request.startDate != null && request.endDate != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndDateRangeAndType(
                    symbol.symbol, true, request.startDate, request.endDate, pageable
                )
            }
            request.year != null && request.quarter != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndQuarter(
                    symbol.symbol, request.year, request.quarter, pageable
                )
            }
            request.year != null -> {
                incomeStatementService.getIncomeStatementsBySymbolAndYearAndType(
                    symbol.symbol, request.year, true, pageable
                )
            }
            else -> {
                incomeStatementService.getIncomeStatementsBySymbolAndType(
                    symbol.symbol, true, pageable
                )
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { IncomeStatementResponse.from(it) }
        )
    }
}
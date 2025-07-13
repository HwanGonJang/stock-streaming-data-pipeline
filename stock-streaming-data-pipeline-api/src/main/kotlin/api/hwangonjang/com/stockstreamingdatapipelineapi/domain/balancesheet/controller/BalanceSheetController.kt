package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.controller

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.controller.request.BalanceSheetRequest
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.controller.response.BalanceSheetResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.service.BalanceSheetService
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
@Tag(name = "💰 Balance Sheet API", description = "대차대조표 API")
@RequestMapping("/v1/balance-sheets")
class BalanceSheetController(
    private val traceIdResolver: TraceIdResolver,
    private val balanceSheetService: BalanceSheetService
) {
    
    @Operation(
        summary = "Get income statements by symbol",
        description = "특정 종목의 대차대조표 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}")
    fun getBalanceSheetsBySymbol(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: BalanceSheetRequest,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<BalanceSheetResponse>> {
        val incomeStatements = when {
            request.startDate != null && request.endDate != null && request.isQuarterly != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndDateRangeAndType(
                    symbol.symbol, request.isQuarterly, request.startDate, request.endDate, pageable
                )
            }
            request.startDate != null && request.endDate != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndDateRange(
                    symbol.symbol, request.startDate, request.endDate, pageable
                )
            }
            request.year != null && request.quarter != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndQuarter(
                    symbol.symbol, request.year, request.quarter, pageable
                )
            }
            request.year != null && request.isQuarterly != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndYearAndType(
                    symbol.symbol, request.year, request.isQuarterly, pageable
                )
            }
            request.year != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndYear(
                    symbol.symbol, request.year, pageable
                )
            }
            request.isQuarterly != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndType(
                    symbol.symbol, request.isQuarterly, pageable
                )
            }
            else -> {
                balanceSheetService.getBalanceSheetsBySymbol(symbol.symbol, pageable)
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { BalanceSheetResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get latest income statement",
        description = "특정 종목의 최신 대차대조표 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Not Found")
        ]
    )
    @GetMapping("/{symbol}/latest")
    fun getLatestBalanceSheet(
        @PathVariable symbol: StockSymbol,
        @RequestParam(defaultValue = "false") isQuarterly: Boolean
    ): StockApiResponse<BalanceSheetResponse> {
        val latestBalanceSheet = balanceSheetService.getLatestBalanceSheet(symbol.symbol, isQuarterly)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = BalanceSheetResponse.from(latestBalanceSheet)
        )
    }
    
    @Operation(
        summary = "Get income statements by year",
        description = "연도별 모든 종목의 대차대조표 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/year/{year}")
    fun getBalanceSheetsByYear(
        @PathVariable year: Int,
        @RequestParam(required = false) isQuarterly: Boolean?,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<BalanceSheetResponse>> {
        val incomeStatements = if (isQuarterly != null) {
            balanceSheetService.getBalanceSheetsByYearAndType(year, isQuarterly, pageable)
        } else {
            balanceSheetService.getBalanceSheetsByYear(year, pageable)
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { BalanceSheetResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get annual income statements",
        description = "연간 대차대조표 조회 (isQuarterly=false)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}/annual")
    fun getAnnualBalanceSheets(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: BalanceSheetRequest,
        @PageableDefault(size = 20) pageable: Pageable
    ): StockApiResponse<Page<BalanceSheetResponse>> {
        val incomeStatements = when {
            request.startDate != null && request.endDate != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndDateRangeAndType(
                    symbol.symbol, false, request.startDate, request.endDate, pageable
                )
            }
            request.year != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndYearAndType(
                    symbol.symbol, request.year, false, pageable
                )
            }
            else -> {
                balanceSheetService.getBalanceSheetsBySymbolAndType(
                    symbol.symbol, false, pageable
                )
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { BalanceSheetResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get quarterly income statements",
        description = "분기별 대차대조표 조회 (isQuarterly=true)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/{symbol}/quarterly")
    fun getQuarterlyBalanceSheets(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: BalanceSheetRequest,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<BalanceSheetResponse>> {
        val incomeStatements = when {
            request.startDate != null && request.endDate != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndDateRangeAndType(
                    symbol.symbol, true, request.startDate, request.endDate, pageable
                )
            }
            request.year != null && request.quarter != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndQuarter(
                    symbol.symbol, request.year, request.quarter, pageable
                )
            }
            request.year != null -> {
                balanceSheetService.getBalanceSheetsBySymbolAndYearAndType(
                    symbol.symbol, request.year, true, pageable
                )
            }
            else -> {
                balanceSheetService.getBalanceSheetsBySymbolAndType(
                    symbol.symbol, true, pageable
                )
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = incomeStatements.map { BalanceSheetResponse.from(it) }
        )
    }
}
package api.hwangonjang.com.stockstreamingdatapipelineapi.support.exception

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.exception.BalanceSheetNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.exception.CashFlowNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.exception.CompanyOverviewNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.exception.DailyPriceNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.exception.IncomeStatementNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.exception.NewsNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.exception.StockNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockrecommendation.exception.StockRecommendationNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.logging.TraceIdResolver
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.response.ErrorResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.response.StockApiResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.response.StockResponseCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
    private val traceIdResolver: TraceIdResolver,
) {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(StockNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleStockNotFoundException(ex: StockNotFoundException): StockApiResponse<ErrorResponse> {
        log.error("StockNotFoundException: {}", ex.message, ex)

        val message = ex.message ?: "Could not find stock."
        val body = ErrorResponse(message)

        return StockApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = StockResponseCode.STOCK_01,
            body = body
        )
    }

    @ExceptionHandler(BalanceSheetNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleBalanceSheetNotFoundException(ex: BalanceSheetNotFoundException): StockApiResponse<ErrorResponse> {
        log.error("BalanceSheetNotFoundException: {}", ex.message, ex)

        val message = ex.message ?: "Could not find balance sheet."
        val body = ErrorResponse(message)

        return StockApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = StockResponseCode.STOCK_02,
            body = body
        )
    }

    @ExceptionHandler(CashFlowNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleCashFlowNotFoundException(ex: CashFlowNotFoundException): StockApiResponse<ErrorResponse> {
        log.error("CashFlowNotFoundException: {}", ex.message, ex)

        val message = ex.message ?: "Could not find cash flow."
        val body = ErrorResponse(message)

        return StockApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = StockResponseCode.STOCK_03,
            body = body
        )
    }

    @ExceptionHandler(CompanyOverviewNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleCompanyOverviewNotFoundException(ex: CompanyOverviewNotFoundException): StockApiResponse<ErrorResponse> {
        log.error("CompanyOverviewNotFoundException: {}", ex.message, ex)

        val message = ex.message ?: "Could not find company overview."
        val body = ErrorResponse(message)

        return StockApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = StockResponseCode.STOCK_04,
            body = body
        )
    }

    @ExceptionHandler(DailyPriceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleDailyPriceNotFoundException(ex: DailyPriceNotFoundException): StockApiResponse<ErrorResponse> {
        log.error("DailyPriceNotFoundException: {}", ex.message, ex)

        val message = ex.message ?: "Could not find daily price."
        val body = ErrorResponse(message)

        return StockApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = StockResponseCode.STOCK_05,
            body = body
        )
    }

    @ExceptionHandler(IncomeStatementNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleIncomeStatementNotFoundException(ex: IncomeStatementNotFoundException): StockApiResponse<ErrorResponse> {
        log.error("IncomeStatementNotFoundException: {}", ex.message, ex)

        val message = ex.message ?: "Could not find income statement."
        val body = ErrorResponse(message)

        return StockApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = StockResponseCode.STOCK_06,
            body = body
        )
    }

    @ExceptionHandler(NewsNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNewsNotFoundException(ex: NewsNotFoundException): StockApiResponse<ErrorResponse> {
        log.error("NewsNotFoundException: {}", ex.message, ex)

        val message = ex.message ?: "Could not find news."
        val body = ErrorResponse(message)

        return StockApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = StockResponseCode.STOCK_07,
            body = body
        )
    }

    @ExceptionHandler(StockRecommendationNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleStockRecommendationNotFoundException(ex: StockRecommendationNotFoundException): StockApiResponse<ErrorResponse> {
        log.error("StockRecommendationNotFoundException: {}", ex.message, ex)

        val message = ex.message ?: "Could not find stock recommendation."
        val body = ErrorResponse(message)

        return StockApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = StockResponseCode.STOCK_08,
            body = body
        )
    }
}
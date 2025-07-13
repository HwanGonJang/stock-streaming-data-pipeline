package api.hwangonjang.com.stockstreamingdatapipelineapi.support.exception

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.exception.StockNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.logging.TraceIdResolver
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.response.ErrorResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.response.StockApiResponse
import com.example.anbdapi.support.response.UserResponseCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class StockExceptionHandler(
    private val traceIdResolver: TraceIdResolver,
) {

    private val log = LoggerFactory.getLogger(StockExceptionHandler::class.java)
    @ExceptionHandler(StockNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUserNotFoundException(ex: StockNotFoundException): StockApiResponse<ErrorResponse> {
        log.error("StockNotFoundException: {}", ex.message, ex)

        val message = ex.message ?: "Could not find stock symbol."
        val body = ErrorResponse(message)

        return StockApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = UserResponseCode.USER_01,
            body = body
        )
    }
}
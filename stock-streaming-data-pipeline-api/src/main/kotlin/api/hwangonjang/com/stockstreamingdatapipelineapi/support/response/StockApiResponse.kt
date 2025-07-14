package api.hwangonjang.com.stockstreamingdatapipelineapi.support.response

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class StockApiResponse<T>(
    val traceId: String,
    val status: Int,
    val code: String,
    val timestamp: LocalDateTime,
    val body: T?,
) {
    companion object {
        fun <T> of(
            traceId: String,
            status: HttpStatus,
            code: String,
            body: T,
        ): StockApiResponse<T> = StockApiResponse(
            traceId = traceId,
            status = status.value(),
            code = code,
            timestamp = LocalDateTime.now(),
            body = body
        )

        fun <T> success(
            traceId: String,
            body: T
        ): StockApiResponse<T> = StockApiResponse(
            traceId = traceId,
            status = HttpStatus.OK.value(),
            code = CommonResponseCode.COMMON_01,
            timestamp = LocalDateTime.now(),
            body = body
        )

        fun <T> success(
            traceId: String,
            status: HttpStatus,
            body: T
        ): StockApiResponse<T> = StockApiResponse(
            traceId = traceId,
            status = status.value(),
            code = CommonResponseCode.COMMON_01,
            timestamp = LocalDateTime.now(),
            body = body
        )

        const val SUCCESS = "OK"
    }
}

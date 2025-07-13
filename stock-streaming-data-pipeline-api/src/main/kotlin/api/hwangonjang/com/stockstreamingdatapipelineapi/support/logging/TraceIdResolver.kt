package api.hwangonjang.com.stockstreamingdatapipelineapi.support.logging

import io.micrometer.tracing.Tracer
import org.springframework.stereotype.Component

@Component
class TraceIdResolver(
    private val tracer: Tracer,
) {
    fun getTraceId(): String {
        val span = tracer.currentSpan() ?: return System.currentTimeMillis().toString()
        return span.context().traceId()
    }
}
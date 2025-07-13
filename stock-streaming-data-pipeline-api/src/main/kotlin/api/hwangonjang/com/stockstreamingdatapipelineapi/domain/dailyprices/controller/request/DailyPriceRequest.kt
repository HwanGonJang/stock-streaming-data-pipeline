package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dailyprices.controller.request

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class DailyPriceRequest(
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate? = null,
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate? = null,
    
    val year: Int? = null,
    val quarter: Int? = null,
    val month: Int? = null,
    val minVolume: Long? = null,
    val sortOrder: String = "desc"
)
package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.controller.request

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class BalanceSheetRequest(
    val isQuarterly: Boolean? = null,
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate? = null,
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate? = null,
    
    val year: Int? = null,
    val quarter: Int? = null,
)
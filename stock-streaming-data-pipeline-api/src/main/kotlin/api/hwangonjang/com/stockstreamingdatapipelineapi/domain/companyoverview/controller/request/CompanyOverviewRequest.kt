package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.controller.request

import java.math.BigDecimal

data class CompanyOverviewRequest(
    val sector: String? = null,
    val industry: String? = null,
    val country: String? = null,
    val minMarketCap: Long? = null,
    val minPE: BigDecimal? = null,
    val maxPE: BigDecimal? = null,
    val minDividendYield: BigDecimal? = null,
    val minROE: BigDecimal? = null,
    val minBeta: BigDecimal? = null,
    val maxBeta: BigDecimal? = null
)
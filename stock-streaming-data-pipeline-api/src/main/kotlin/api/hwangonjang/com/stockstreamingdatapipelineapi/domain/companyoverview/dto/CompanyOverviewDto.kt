package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.entity.CompanyOverview
import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class CompanyOverviewDto(
    val symbol: String,
    val description: String?,
    val currency: String?,
    val country: String?,
    val sector: String?,
    val industry: String?,
    val address: String?,
    val fiscalYearEnd: String?,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val latestQuarter: LocalDate?,
    
    val marketCapitalization: Long?,
    val ebitda: Long?,
    val peRatio: BigDecimal?,
    val pegRatio: BigDecimal?,
    val bookValue: BigDecimal?,
    val dividendPerShare: BigDecimal?,
    val dividendYield: BigDecimal?,
    val eps: BigDecimal?,
    val revenuePerShareTtm: BigDecimal?,
    val profitMargin: BigDecimal?,
    val operatingMarginTtm: BigDecimal?,
    val returnOnAssetsTtm: BigDecimal?,
    val returnOnEquityTtm: BigDecimal?,
    val revenueTtm: Long?,
    val grossProfitTtm: Long?,
    val dilutedEpsTtm: BigDecimal?,
    val quarterlyEarningsGrowthYoy: BigDecimal?,
    val quarterlyRevenueGrowthYoy: BigDecimal?,
    val analystTargetPrice: BigDecimal?,
    val trailingPe: BigDecimal?,
    val forwardPe: BigDecimal?,
    val priceToSalesRatioTtm: BigDecimal?,
    val priceToBookRatio: BigDecimal?,
    val evToRevenue: BigDecimal?,
    val evToEbitda: BigDecimal?,
    val beta: BigDecimal?,
    val fiftyTwoWeekHigh: BigDecimal?,
    val fiftyTwoWeekLow: BigDecimal?,
    val fiftyDayMovingAverage: BigDecimal?,
    val twoHundredDayMovingAverage: BigDecimal?,
    val sharesOutstanding: Long?,
    val sharesFloat: Long?,
    val sharesShort: Long?,
    val sharesShortPriorMonth: Long?,
    val shortRatio: BigDecimal?,
    val shortPercentOutstanding: BigDecimal?,
    val shortPercentFloat: BigDecimal?,
    val percentInsiders: BigDecimal?,
    val percentInstitutions: BigDecimal?,
    val forwardAnnualDividendRate: BigDecimal?,
    val forwardAnnualDividendYield: BigDecimal?,
    val payoutRatio: BigDecimal?,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val dividendDate: LocalDate?,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val exDividendDate: LocalDate?,
    
    val lastSplitFactor: String?,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    val lastSplitDate: LocalDate?,
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    val lastUpdated: LocalDateTime?
) {
    companion object {
        fun from(entity: CompanyOverview): CompanyOverviewDto {
            return CompanyOverviewDto(
                symbol = entity.symbol,
                description = entity.description,
                currency = entity.currency,
                country = entity.country,
                sector = entity.sector,
                industry = entity.industry,
                address = entity.address,
                fiscalYearEnd = entity.fiscalYearEnd,
                latestQuarter = entity.latestQuarter,
                marketCapitalization = entity.marketCapitalization,
                ebitda = entity.ebitda,
                peRatio = entity.peRatio,
                pegRatio = entity.pegRatio,
                bookValue = entity.bookValue,
                dividendPerShare = entity.dividendPerShare,
                dividendYield = entity.dividendYield,
                eps = entity.eps,
                revenuePerShareTtm = entity.revenuePerShareTtm,
                profitMargin = entity.profitMargin,
                operatingMarginTtm = entity.operatingMarginTtm,
                returnOnAssetsTtm = entity.returnOnAssetsTtm,
                returnOnEquityTtm = entity.returnOnEquityTtm,
                revenueTtm = entity.revenueTtm,
                grossProfitTtm = entity.grossProfitTtm,
                dilutedEpsTtm = entity.dilutedEpsTtm,
                quarterlyEarningsGrowthYoy = entity.quarterlyEarningsGrowthYoy,
                quarterlyRevenueGrowthYoy = entity.quarterlyRevenueGrowthYoy,
                analystTargetPrice = entity.analystTargetPrice,
                trailingPe = entity.trailingPe,
                forwardPe = entity.forwardPe,
                priceToSalesRatioTtm = entity.priceToSalesRatioTtm,
                priceToBookRatio = entity.priceToBookRatio,
                evToRevenue = entity.evToRevenue,
                evToEbitda = entity.evToEbitda,
                beta = entity.beta,
                fiftyTwoWeekHigh = entity.fiftyTwoWeekHigh,
                fiftyTwoWeekLow = entity.fiftyTwoWeekLow,
                fiftyDayMovingAverage = entity.fiftyDayMovingAverage,
                twoHundredDayMovingAverage = entity.twoHundredDayMovingAverage,
                sharesOutstanding = entity.sharesOutstanding,
                sharesFloat = entity.sharesFloat,
                sharesShort = entity.sharesShort,
                sharesShortPriorMonth = entity.sharesShortPriorMonth,
                shortRatio = entity.shortRatio,
                shortPercentOutstanding = entity.shortPercentOutstanding,
                shortPercentFloat = entity.shortPercentFloat,
                percentInsiders = entity.percentInsiders,
                percentInstitutions = entity.percentInstitutions,
                forwardAnnualDividendRate = entity.forwardAnnualDividendRate,
                forwardAnnualDividendYield = entity.forwardAnnualDividendYield,
                payoutRatio = entity.payoutRatio,
                dividendDate = entity.dividendDate,
                exDividendDate = entity.exDividendDate,
                lastSplitFactor = entity.lastSplitFactor,
                lastSplitDate = entity.lastSplitDate,
                lastUpdated = entity.lastUpdated
            )
        }
    }
}
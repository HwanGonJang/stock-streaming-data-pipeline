package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "company_overview")
class CompanyOverview(
    @Id
    @Column(name = "symbol", nullable = false, unique = true, length = 10)
    val symbol: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "currency", length = 10)
    val currency: String? = null,

    @Column(name = "country", length = 50)
    val country: String? = null,

    @Column(name = "sector", length = 100)
    val sector: String? = null,

    @Column(name = "industry", length = 100)
    val industry: String? = null,

    @Column(name = "address", columnDefinition = "TEXT")
    val address: String? = null,

    @Column(name = "fiscal_year_end", length = 10)
    val fiscalYearEnd: String? = null,

    @Column(name = "latest_quarter")
    val latestQuarter: LocalDate? = null,

    @Column(name = "market_capitalization")
    val marketCapitalization: Long? = null,

    @Column(name = "ebitda")
    val ebitda: Long? = null,

    @Column(name = "pe_ratio", precision = 12, scale = 4)
    val peRatio: BigDecimal? = null,

    @Column(name = "peg_ratio", precision = 12, scale = 4)
    val pegRatio: BigDecimal? = null,

    @Column(name = "book_value", precision = 12, scale = 4)
    val bookValue: BigDecimal? = null,

    @Column(name = "dividend_per_share", precision = 12, scale = 4)
    val dividendPerShare: BigDecimal? = null,

    @Column(name = "dividend_yield", precision = 12, scale = 4)
    val dividendYield: BigDecimal? = null,

    @Column(name = "eps", precision = 12, scale = 4)
    val eps: BigDecimal? = null,

    @Column(name = "revenue_per_share_ttm", precision = 12, scale = 4)
    val revenuePerShareTtm: BigDecimal? = null,

    @Column(name = "profit_margin", precision = 12, scale = 4)
    val profitMargin: BigDecimal? = null,

    @Column(name = "operating_margin_ttm", precision = 12, scale = 4)
    val operatingMarginTtm: BigDecimal? = null,

    @Column(name = "return_on_assets_ttm", precision = 12, scale = 4)
    val returnOnAssetsTtm: BigDecimal? = null,

    @Column(name = "return_on_equity_ttm", precision = 12, scale = 4)
    val returnOnEquityTtm: BigDecimal? = null,

    @Column(name = "revenue_ttm")
    val revenueTtm: Long? = null,

    @Column(name = "gross_profit_ttm")
    val grossProfitTtm: Long? = null,

    @Column(name = "diluted_eps_ttm", precision = 12, scale = 4)
    val dilutedEpsTtm: BigDecimal? = null,

    @Column(name = "quarterly_earnings_growth_yoy", precision = 12, scale = 4)
    val quarterlyEarningsGrowthYoy: BigDecimal? = null,

    @Column(name = "quarterly_revenue_growth_yoy", precision = 12, scale = 4)
    val quarterlyRevenueGrowthYoy: BigDecimal? = null,

    @Column(name = "analyst_target_price", precision = 12, scale = 4)
    val analystTargetPrice: BigDecimal? = null,

    @Column(name = "trailing_pe", precision = 12, scale = 4)
    val trailingPe: BigDecimal? = null,

    @Column(name = "forward_pe", precision = 12, scale = 4)
    val forwardPe: BigDecimal? = null,

    @Column(name = "price_to_sales_ratio_ttm", precision = 12, scale = 4)
    val priceToSalesRatioTtm: BigDecimal? = null,

    @Column(name = "price_to_book_ratio", precision = 12, scale = 4)
    val priceToBookRatio: BigDecimal? = null,

    @Column(name = "ev_to_revenue", precision = 12, scale = 4)
    val evToRevenue: BigDecimal? = null,

    @Column(name = "ev_to_ebitda", precision = 12, scale = 4)
    val evToEbitda: BigDecimal? = null,

    @Column(name = "beta", precision = 12, scale = 4)
    val beta: BigDecimal? = null,

    @Column(name = "fifty_two_week_high", precision = 12, scale = 4)
    val fiftyTwoWeekHigh: BigDecimal? = null,

    @Column(name = "fifty_two_week_low", precision = 12, scale = 4)
    val fiftyTwoWeekLow: BigDecimal? = null,

    @Column(name = "fifty_day_moving_average", precision = 12, scale = 4)
    val fiftyDayMovingAverage: BigDecimal? = null,

    @Column(name = "two_hundred_day_moving_average", precision = 12, scale = 4)
    val twoHundredDayMovingAverage: BigDecimal? = null,

    @Column(name = "shares_outstanding")
    val sharesOutstanding: Long? = null,

    @Column(name = "shares_float")
    val sharesFloat: Long? = null,

    @Column(name = "shares_short")
    val sharesShort: Long? = null,

    @Column(name = "shares_short_prior_month")
    val sharesShortPriorMonth: Long? = null,

    @Column(name = "short_ratio", precision = 12, scale = 4)
    val shortRatio: BigDecimal? = null,

    @Column(name = "short_percent_outstanding", precision = 12, scale = 4)
    val shortPercentOutstanding: BigDecimal? = null,

    @Column(name = "short_percent_float", precision = 12, scale = 4)
    val shortPercentFloat: BigDecimal? = null,

    @Column(name = "percent_insiders", precision = 12, scale = 4)
    val percentInsiders: BigDecimal? = null,

    @Column(name = "percent_institutions", precision = 12, scale = 4)
    val percentInstitutions: BigDecimal? = null,

    @Column(name = "forward_annual_dividend_rate", precision = 12, scale = 4)
    val forwardAnnualDividendRate: BigDecimal? = null,

    @Column(name = "forward_annual_dividend_yield", precision = 12, scale = 4)
    val forwardAnnualDividendYield: BigDecimal? = null,

    @Column(name = "payout_ratio", precision = 12, scale = 4)
    val payoutRatio: BigDecimal? = null,

    @Column(name = "dividend_date")
    val dividendDate: LocalDate? = null,

    @Column(name = "ex_dividend_date")
    val exDividendDate: LocalDate? = null,

    @Column(name = "last_split_factor", length = 20)
    val lastSplitFactor: String? = null,

    @Column(name = "last_split_date")
    val lastSplitDate: LocalDate? = null,

    @LastModifiedDate
    @Column(name = "last_updated")
    val lastUpdated: LocalDateTime? = null
)
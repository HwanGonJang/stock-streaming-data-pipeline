package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.entity

import jakarta.persistence.*
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "balance_sheets")
class BalanceSheet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "symbol", nullable = false, length = 10)
    val symbol: String,

    @Column(name = "fiscal_date_ending", nullable = false)
    val fiscalDateEnding: LocalDate,

    @Column(name = "reported_currency", length = 10)
    val reportedCurrency: String? = null,

    @Column(name = "total_assets")
    val totalAssets: Long? = null,

    @Column(name = "total_current_assets")
    val totalCurrentAssets: Long? = null,

    @Column(name = "cash_and_cash_equivalents_at_carrying_value")
    val cashAndCashEquivalentsAtCarryingValue: Long? = null,

    @Column(name = "cash_and_short_term_investments")
    val cashAndShortTermInvestments: Long? = null,

    @Column(name = "inventory")
    val inventory: Long? = null,

    @Column(name = "current_net_receivables")
    val currentNetReceivables: Long? = null,

    @Column(name = "total_non_current_assets")
    val totalNonCurrentAssets: Long? = null,

    @Column(name = "property_plant_equipment")
    val propertyPlantEquipment: Long? = null,

    @Column(name = "accumulated_depreciation_amortization_ppe")
    val accumulatedDepreciationAmortizationPpe: Long? = null,

    @Column(name = "intangible_assets")
    val intangibleAssets: Long? = null,

    @Column(name = "intangible_assets_excluding_goodwill")
    val intangibleAssetsExcludingGoodwill: Long? = null,

    @Column(name = "goodwill")
    val goodwill: Long? = null,

    @Column(name = "investments")
    val investments: Long? = null,

    @Column(name = "long_term_investments")
    val longTermInvestments: Long? = null,

    @Column(name = "short_term_investments")
    val shortTermInvestments: Long? = null,

    @Column(name = "other_current_assets")
    val otherCurrentAssets: Long? = null,

    @Column(name = "other_non_current_assets")
    val otherNonCurrentAssets: Long? = null,

    @Column(name = "total_liabilities")
    val totalLiabilities: Long? = null,

    @Column(name = "total_current_liabilities")
    val totalCurrentLiabilities: Long? = null,

    @Column(name = "current_accounts_payable")
    val currentAccountsPayable: Long? = null,

    @Column(name = "deferred_revenue")
    val deferredRevenue: Long? = null,

    @Column(name = "current_debt")
    val currentDebt: Long? = null,

    @Column(name = "short_term_debt")
    val shortTermDebt: Long? = null,

    @Column(name = "total_non_current_liabilities")
    val totalNonCurrentLiabilities: Long? = null,

    @Column(name = "capital_lease_obligations")
    val capitalLeaseObligations: Long? = null,

    @Column(name = "long_term_debt")
    val longTermDebt: Long? = null,

    @Column(name = "current_long_term_debt")
    val currentLongTermDebt: Long? = null,

    @Column(name = "long_term_debt_noncurrent")
    val longTermDebtNoncurrent: Long? = null,

    @Column(name = "short_long_term_debt_total")
    val shortLongTermDebtTotal: Long? = null,

    @Column(name = "other_current_liabilities")
    val otherCurrentLiabilities: Long? = null,

    @Column(name = "other_non_current_liabilities")
    val otherNonCurrentLiabilities: Long? = null,

    @Column(name = "total_shareholder_equity")
    val totalShareholderEquity: Long? = null,

    @Column(name = "treasury_stock")
    val treasuryStock: Long? = null,

    @Column(name = "retained_earnings")
    val retainedEarnings: Long? = null,

    @Column(name = "common_stock")
    val commonStock: Long? = null,

    @Column(name = "common_stock_shares_outstanding")
    val commonStockSharesOutstanding: Long? = null,

    @Column(name = "is_quarterly", nullable = false)
    val isQuarterly: Boolean,

    @LastModifiedDate
    @Column(name = "last_updated")
    val lastUpdated: LocalDateTime? = null
)
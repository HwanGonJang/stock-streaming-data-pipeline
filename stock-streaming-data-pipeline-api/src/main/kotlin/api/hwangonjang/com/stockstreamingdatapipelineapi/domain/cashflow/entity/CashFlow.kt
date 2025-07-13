package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.entity

import jakarta.persistence.*
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "cash_flows")
class CashFlow(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "symbol", nullable = false, length = 10)
    val symbol: String,

    @Column(name = "fiscal_date_ending", nullable = false)
    val fiscalDateEnding: LocalDate,

    @Column(name = "reported_currency", length = 10)
    val reportedCurrency: String? = null,

    @Column(name = "operating_cashflow")
    val operatingCashflow: Long? = null,

    @Column(name = "payments_for_operating_activities")
    val paymentsForOperatingActivities: Long? = null,

    @Column(name = "proceeds_from_operating_activities")
    val proceedsFromOperatingActivities: Long? = null,

    @Column(name = "change_in_operating_liabilities")
    val changeInOperatingLiabilities: Long? = null,

    @Column(name = "change_in_operating_assets")
    val changeInOperatingAssets: Long? = null,

    @Column(name = "depreciation_depletion_and_amortization")
    val depreciationDepletionAndAmortization: Long? = null,

    @Column(name = "capital_expenditures")
    val capitalExpenditures: Long? = null,

    @Column(name = "change_in_receivables")
    val changeInReceivables: Long? = null,

    @Column(name = "change_in_inventory")
    val changeInInventory: Long? = null,

    @Column(name = "profit_loss")
    val profitLoss: Long? = null,

    @Column(name = "cashflow_from_investment")
    val cashflowFromInvestment: Long? = null,

    @Column(name = "cashflow_from_financing")
    val cashflowFromFinancing: Long? = null,

    @Column(name = "proceeds_from_repayments_of_short_term_debt")
    val proceedsFromRepaymentsOfShortTermDebt: Long? = null,

    @Column(name = "payments_for_repurchase_of_common_stock")
    val paymentsForRepurchaseOfCommonStock: Long? = null,

    @Column(name = "payments_for_repurchase_of_equity")
    val paymentsForRepurchaseOfEquity: Long? = null,

    @Column(name = "payments_for_repurchase_of_preferred_stock")
    val paymentsForRepurchaseOfPreferredStock: Long? = null,

    @Column(name = "dividend_payout")
    val dividendPayout: Long? = null,

    @Column(name = "dividend_payout_common_stock")
    val dividendPayoutCommonStock: Long? = null,

    @Column(name = "dividend_payout_preferred_stock")
    val dividendPayoutPreferredStock: Long? = null,

    @Column(name = "proceeds_from_issuance_of_common_stock")
    val proceedsFromIssuanceOfCommonStock: Long? = null,

    @Column(name = "proceeds_from_issuance_of_long_term_debt_and_capital_securities")
    val proceedsFromIssuanceOfLongTermDebtAndCapitalSecurities: Long? = null,

    @Column(name = "proceeds_from_issuance_of_preferred_stock")
    val proceedsFromIssuanceOfPreferredStock: Long? = null,

    @Column(name = "proceeds_from_repurchase_of_equity")
    val proceedsFromRepurchaseOfEquity: Long? = null,

    @Column(name = "proceeds_from_sale_of_treasury_stock")
    val proceedsFromSaleOfTreasuryStock: Long? = null,

    @Column(name = "change_in_cash_and_cash_equivalents")
    val changeInCashAndCashEquivalents: Long? = null,

    @Column(name = "change_in_exchange_rate")
    val changeInExchangeRate: Long? = null,

    @Column(name = "net_income")
    val netIncome: Long? = null,

    @Column(name = "is_quarterly", nullable = false)
    val isQuarterly: Boolean,

    @LastModifiedDate
    @Column(name = "last_updated")
    val lastUpdated: LocalDateTime? = null
)
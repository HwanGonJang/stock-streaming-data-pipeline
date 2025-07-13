package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.entity

import jakarta.persistence.*
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "income_statements")
class IncomeStatement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "symbol", nullable = false, unique = true, length = 10)
    val symbol: String,

    @Column(name = "fiscal_date_ending", nullable = false)
    val fiscalDateEnding: LocalDate,

    @Column(name = "reported_currency", length = 10)
    val reportedCurrency: String? = null,

    @Column(name = "gross_profit")
    val grossProfit: Long? = null,

    @Column(name = "total_revenue")
    val totalRevenue: Long? = null,

    @Column(name = "cost_of_revenue")
    val costOfRevenue: Long? = null,

    @Column(name = "cost_of_goods_and_services_sold")
    val costOfGoodsAndServicesSold: Long? = null,

    @Column(name = "operating_income")
    val operatingIncome: Long? = null,

    @Column(name = "selling_general_and_administrative")
    val sellingGeneralAndAdministrative: Long? = null,

    @Column(name = "research_and_development")
    val researchAndDevelopment: Long? = null,

    @Column(name = "operating_expenses")
    val operatingExpenses: Long? = null,

    @Column(name = "investment_income_net")
    val investmentIncomeNet: Long? = null,

    @Column(name = "net_interest_income")
    val netInterestIncome: Long? = null,

    @Column(name = "interest_income")
    val interestIncome: Long? = null,

    @Column(name = "interest_expense")
    val interestExpense: Long? = null,

    @Column(name = "non_interest_income")
    val nonInterestIncome: Long? = null,

    @Column(name = "other_non_operating_income")
    val otherNonOperatingIncome: Long? = null,

    @Column(name = "depreciation")
    val depreciation: Long? = null,

    @Column(name = "depreciation_and_amortization")
    val depreciationAndAmortization: Long? = null,

    @Column(name = "income_before_tax")
    val incomeBeforeTax: Long? = null,

    @Column(name = "income_tax_expense")
    val incomeTaxExpense: Long? = null,

    @Column(name = "interest_and_debt_expense")
    val interestAndDebtExpense: Long? = null,

    @Column(name = "net_income_from_continuing_operations")
    val netIncomeFromContinuingOperations: Long? = null,

    @Column(name = "comprehensive_income_net_of_tax")
    val comprehensiveIncomeNetOfTax: Long? = null,

    @Column(name = "ebit")
    val ebit: Long? = null,

    @Column(name = "ebitda")
    val ebitda: Long? = null,

    @Column(name = "net_income")
    val netIncome: Long? = null,

    @Column(name = "is_quarterly", nullable = false)
    val isQuarterly: Boolean,

    @LastModifiedDate
    @Column(name = "last_updated")
    val lastUpdated: LocalDateTime? = null
)
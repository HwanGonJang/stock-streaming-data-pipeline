package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.controller.response

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.dto.IncomeStatementDto
import java.time.LocalDate
import java.time.LocalDateTime

data class IncomeStatementResponse(
    val id: Long?,
    val symbol: String,
    val fiscalDateEnding: LocalDate,
    val reportedCurrency: String?,
    val grossProfit: Long?,
    val totalRevenue: Long?,
    val costOfRevenue: Long?,
    val costOfGoodsAndServicesSold: Long?,
    val operatingIncome: Long?,
    val sellingGeneralAndAdministrative: Long?,
    val researchAndDevelopment: Long?,
    val operatingExpenses: Long?,
    val investmentIncomeNet: Long?,
    val netInterestIncome: Long?,
    val interestIncome: Long?,
    val interestExpense: Long?,
    val nonInterestIncome: Long?,
    val otherNonOperatingIncome: Long?,
    val depreciation: Long?,
    val depreciationAndAmortization: Long?,
    val incomeBeforeTax: Long?,
    val incomeTaxExpense: Long?,
    val interestAndDebtExpense: Long?,
    val netIncomeFromContinuingOperations: Long?,
    val comprehensiveIncomeNetOfTax: Long?,
    val ebit: Long?,
    val ebitda: Long?,
    val netIncome: Long?,
    val isQuarterly: Boolean,
    val lastUpdated: LocalDateTime?
) {
    companion object {
        fun from(dto: IncomeStatementDto): IncomeStatementResponse {
            return IncomeStatementResponse(
                id = dto.id,
                symbol = dto.symbol,
                fiscalDateEnding = dto.fiscalDateEnding,
                reportedCurrency = dto.reportedCurrency,
                grossProfit = dto.grossProfit,
                totalRevenue = dto.totalRevenue,
                costOfRevenue = dto.costOfRevenue,
                costOfGoodsAndServicesSold = dto.costOfGoodsAndServicesSold,
                operatingIncome = dto.operatingIncome,
                sellingGeneralAndAdministrative = dto.sellingGeneralAndAdministrative,
                researchAndDevelopment = dto.researchAndDevelopment,
                operatingExpenses = dto.operatingExpenses,
                investmentIncomeNet = dto.investmentIncomeNet,
                netInterestIncome = dto.netInterestIncome,
                interestIncome = dto.interestIncome,
                interestExpense = dto.interestExpense,
                nonInterestIncome = dto.nonInterestIncome,
                otherNonOperatingIncome = dto.otherNonOperatingIncome,
                depreciation = dto.depreciation,
                depreciationAndAmortization = dto.depreciationAndAmortization,
                incomeBeforeTax = dto.incomeBeforeTax,
                incomeTaxExpense = dto.incomeTaxExpense,
                interestAndDebtExpense = dto.interestAndDebtExpense,
                netIncomeFromContinuingOperations = dto.netIncomeFromContinuingOperations,
                comprehensiveIncomeNetOfTax = dto.comprehensiveIncomeNetOfTax,
                ebit = dto.ebit,
                ebitda = dto.ebitda,
                netIncome = dto.netIncome,
                isQuarterly = dto.isQuarterly,
                lastUpdated = dto.lastUpdated
            )
        }
    }
}
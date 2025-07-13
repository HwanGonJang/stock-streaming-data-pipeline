package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.incomestatement.entity.IncomeStatement
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime

data class IncomeStatementDto(
    val id: Long?,
    val symbol: String,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
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
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    val lastUpdated: LocalDateTime?
) {
    companion object {
        fun from(entity: IncomeStatement): IncomeStatementDto {
            return IncomeStatementDto(
                id = entity.id,
                symbol = entity.symbol,
                fiscalDateEnding = entity.fiscalDateEnding,
                reportedCurrency = entity.reportedCurrency,
                grossProfit = entity.grossProfit,
                totalRevenue = entity.totalRevenue,
                costOfRevenue = entity.costOfRevenue,
                costOfGoodsAndServicesSold = entity.costOfGoodsAndServicesSold,
                operatingIncome = entity.operatingIncome,
                sellingGeneralAndAdministrative = entity.sellingGeneralAndAdministrative,
                researchAndDevelopment = entity.researchAndDevelopment,
                operatingExpenses = entity.operatingExpenses,
                investmentIncomeNet = entity.investmentIncomeNet,
                netInterestIncome = entity.netInterestIncome,
                interestIncome = entity.interestIncome,
                interestExpense = entity.interestExpense,
                nonInterestIncome = entity.nonInterestIncome,
                otherNonOperatingIncome = entity.otherNonOperatingIncome,
                depreciation = entity.depreciation,
                depreciationAndAmortization = entity.depreciationAndAmortization,
                incomeBeforeTax = entity.incomeBeforeTax,
                incomeTaxExpense = entity.incomeTaxExpense,
                interestAndDebtExpense = entity.interestAndDebtExpense,
                netIncomeFromContinuingOperations = entity.netIncomeFromContinuingOperations,
                comprehensiveIncomeNetOfTax = entity.comprehensiveIncomeNetOfTax,
                ebit = entity.ebit,
                ebitda = entity.ebitda,
                netIncome = entity.netIncome,
                isQuarterly = entity.isQuarterly,
                lastUpdated = entity.lastUpdated
            )
        }
    }
}
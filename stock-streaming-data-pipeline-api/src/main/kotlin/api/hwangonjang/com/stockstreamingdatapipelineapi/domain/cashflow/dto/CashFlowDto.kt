package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.entity.CashFlow
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime

data class CashFlowDto(
    val id: Long?,
    val symbol: String,

    @JsonFormat(pattern = "yyyy-MM-dd")
    val fiscalDateEnding: LocalDate,

    val reportedCurrency: String?,
    val operatingCashflow: Long?,
    val paymentsForOperatingActivities: Long?,
    val proceedsFromOperatingActivities: Long?,
    val changeInOperatingLiabilities: Long?,
    val changeInOperatingAssets: Long?,
    val depreciationDepletionAndAmortization: Long?,
    val capitalExpenditures: Long?,
    val changeInReceivables: Long?,
    val changeInInventory: Long?,
    val profitLoss: Long?,
    val cashflowFromInvestment: Long?,
    val cashflowFromFinancing: Long?,
    val proceedsFromRepaymentsOfShortTermDebt: Long?,
    val paymentsForRepurchaseOfCommonStock: Long?,
    val paymentsForRepurchaseOfEquity: Long?,
    val paymentsForRepurchaseOfPreferredStock: Long?,
    val dividendPayout: Long?,
    val dividendPayoutCommonStock: Long?,
    val dividendPayoutPreferredStock: Long?,
    val proceedsFromIssuanceOfCommonStock: Long?,
    val proceedsFromIssuanceOfLongTermDebtAndCapitalSecurities: Long?,
    val proceedsFromIssuanceOfPreferredStock: Long?,
    val proceedsFromRepurchaseOfEquity: Long?,
    val proceedsFromSaleOfTreasuryStock: Long?,
    val changeInCashAndCashEquivalents: Long?,
    val changeInExchangeRate: Long?,
    val netIncome: Long?,
    val isQuarterly: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    val lastUpdated: LocalDateTime?
) {
    companion object {
        fun from(entity: CashFlow): CashFlowDto {
            return CashFlowDto(
                id = entity.id,
                symbol = entity.symbol,
                fiscalDateEnding = entity.fiscalDateEnding,
                reportedCurrency = entity.reportedCurrency,
                operatingCashflow = entity.operatingCashflow,
                paymentsForOperatingActivities = entity.paymentsForOperatingActivities,
                proceedsFromOperatingActivities = entity.proceedsFromOperatingActivities,
                changeInOperatingLiabilities = entity.changeInOperatingLiabilities,
                changeInOperatingAssets = entity.changeInOperatingAssets,
                depreciationDepletionAndAmortization = entity.depreciationDepletionAndAmortization,
                capitalExpenditures = entity.capitalExpenditures,
                changeInReceivables = entity.changeInReceivables,
                changeInInventory = entity.changeInInventory,
                profitLoss = entity.profitLoss,
                cashflowFromInvestment = entity.cashflowFromInvestment,
                cashflowFromFinancing = entity.cashflowFromFinancing,
                proceedsFromRepaymentsOfShortTermDebt = entity.proceedsFromRepaymentsOfShortTermDebt,
                paymentsForRepurchaseOfCommonStock = entity.paymentsForRepurchaseOfCommonStock,
                paymentsForRepurchaseOfEquity = entity.paymentsForRepurchaseOfEquity,
                paymentsForRepurchaseOfPreferredStock = entity.paymentsForRepurchaseOfPreferredStock,
                dividendPayout = entity.dividendPayout,
                dividendPayoutCommonStock = entity.dividendPayoutCommonStock,
                dividendPayoutPreferredStock = entity.dividendPayoutPreferredStock,
                proceedsFromIssuanceOfCommonStock = entity.proceedsFromIssuanceOfCommonStock,
                proceedsFromIssuanceOfLongTermDebtAndCapitalSecurities = entity.proceedsFromIssuanceOfLongTermDebtAndCapitalSecurities,
                proceedsFromIssuanceOfPreferredStock = entity.proceedsFromIssuanceOfPreferredStock,
                proceedsFromRepurchaseOfEquity = entity.proceedsFromRepurchaseOfEquity,
                proceedsFromSaleOfTreasuryStock = entity.proceedsFromSaleOfTreasuryStock,
                changeInCashAndCashEquivalents = entity.changeInCashAndCashEquivalents,
                changeInExchangeRate = entity.changeInExchangeRate,
                netIncome = entity.netIncome,
                isQuarterly = entity.isQuarterly,
                lastUpdated = entity.lastUpdated
            )
        }
    }
}
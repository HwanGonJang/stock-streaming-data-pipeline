package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.controller.response

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.cashflow.dto.CashFlowDto
import java.time.LocalDate
import java.time.LocalDateTime

data class CashFlowResponse(
    val id: Long?,
    val symbol: String,
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
    val lastUpdated: LocalDateTime?
) {
    companion object {
        fun from(dto: CashFlowDto): CashFlowResponse {
            return CashFlowResponse(
                id = dto.id,
                symbol = dto.symbol,
                fiscalDateEnding = dto.fiscalDateEnding,
                reportedCurrency = dto.reportedCurrency,
                operatingCashflow = dto.operatingCashflow,
                paymentsForOperatingActivities = dto.paymentsForOperatingActivities,
                proceedsFromOperatingActivities = dto.proceedsFromOperatingActivities,
                changeInOperatingLiabilities = dto.changeInOperatingLiabilities,
                changeInOperatingAssets = dto.changeInOperatingAssets,
                depreciationDepletionAndAmortization = dto.depreciationDepletionAndAmortization,
                capitalExpenditures = dto.capitalExpenditures,
                changeInReceivables = dto.changeInReceivables,
                changeInInventory = dto.changeInInventory,
                profitLoss = dto.profitLoss,
                cashflowFromInvestment = dto.cashflowFromInvestment,
                cashflowFromFinancing = dto.cashflowFromFinancing,
                proceedsFromRepaymentsOfShortTermDebt = dto.proceedsFromRepaymentsOfShortTermDebt,
                paymentsForRepurchaseOfCommonStock = dto.paymentsForRepurchaseOfCommonStock,
                paymentsForRepurchaseOfEquity = dto.paymentsForRepurchaseOfEquity,
                paymentsForRepurchaseOfPreferredStock = dto.paymentsForRepurchaseOfPreferredStock,
                dividendPayout = dto.dividendPayout,
                dividendPayoutCommonStock = dto.dividendPayoutCommonStock,
                dividendPayoutPreferredStock = dto.dividendPayoutPreferredStock,
                proceedsFromIssuanceOfCommonStock = dto.proceedsFromIssuanceOfCommonStock,
                proceedsFromIssuanceOfLongTermDebtAndCapitalSecurities = dto.proceedsFromIssuanceOfLongTermDebtAndCapitalSecurities,
                proceedsFromIssuanceOfPreferredStock = dto.proceedsFromIssuanceOfPreferredStock,
                proceedsFromRepurchaseOfEquity = dto.proceedsFromRepurchaseOfEquity,
                proceedsFromSaleOfTreasuryStock = dto.proceedsFromSaleOfTreasuryStock,
                changeInCashAndCashEquivalents = dto.changeInCashAndCashEquivalents,
                changeInExchangeRate = dto.changeInExchangeRate,
                netIncome = dto.netIncome,
                isQuarterly = dto.isQuarterly,
                lastUpdated = dto.lastUpdated
            )
        }
    }
}
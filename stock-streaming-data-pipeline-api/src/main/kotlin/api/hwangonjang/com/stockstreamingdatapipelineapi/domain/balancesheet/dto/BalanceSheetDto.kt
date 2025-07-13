package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.entity.BalanceSheet
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime

data class BalanceSheetDto(
    val id: Long?,
    val symbol: String,

    @JsonFormat(pattern = "yyyy-MM-dd")
    val fiscalDateEnding: LocalDate,

    val reportedCurrency: String?,
    val totalAssets: Long?,
    val totalCurrentAssets: Long?,
    val cashAndCashEquivalentsAtCarryingValue: Long?,
    val cashAndShortTermInvestments: Long?,
    val inventory: Long?,
    val currentNetReceivables: Long?,
    val totalNonCurrentAssets: Long?,
    val propertyPlantEquipment: Long?,
    val accumulatedDepreciationAmortizationPpe: Long?,
    val intangibleAssets: Long?,
    val intangibleAssetsExcludingGoodwill: Long?,
    val goodwill: Long?,
    val investments: Long?,
    val longTermInvestments: Long?,
    val shortTermInvestments: Long?,
    val otherCurrentAssets: Long?,
    val otherNonCurrentAssets: Long?,
    val totalLiabilities: Long?,
    val totalCurrentLiabilities: Long?,
    val currentAccountsPayable: Long?,
    val deferredRevenue: Long?,
    val currentDebt: Long?,
    val shortTermDebt: Long?,
    val totalNonCurrentLiabilities: Long?,
    val capitalLeaseObligations: Long?,
    val longTermDebt: Long?,
    val currentLongTermDebt: Long?,
    val longTermDebtNoncurrent: Long?,
    val shortLongTermDebtTotal: Long?,
    val otherCurrentLiabilities: Long?,
    val otherNonCurrentLiabilities: Long?,
    val totalShareholderEquity: Long?,
    val treasuryStock: Long?,
    val retainedEarnings: Long?,
    val commonStock: Long?,
    val commonStockSharesOutstanding: Long?,
    val isQuarterly: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    val lastUpdated: LocalDateTime?
) {
    companion object {
        fun from(entity: BalanceSheet): BalanceSheetDto {
            return BalanceSheetDto(
                id = entity.id,
                symbol = entity.symbol,
                fiscalDateEnding = entity.fiscalDateEnding,
                reportedCurrency = entity.reportedCurrency,
                totalAssets = entity.totalAssets,
                totalCurrentAssets = entity.totalCurrentAssets,
                cashAndCashEquivalentsAtCarryingValue = entity.cashAndCashEquivalentsAtCarryingValue,
                cashAndShortTermInvestments = entity.cashAndShortTermInvestments,
                inventory = entity.inventory,
                currentNetReceivables = entity.currentNetReceivables,
                totalNonCurrentAssets = entity.totalNonCurrentAssets,
                propertyPlantEquipment = entity.propertyPlantEquipment,
                accumulatedDepreciationAmortizationPpe = entity.accumulatedDepreciationAmortizationPpe,
                intangibleAssets = entity.intangibleAssets,
                intangibleAssetsExcludingGoodwill = entity.intangibleAssetsExcludingGoodwill,
                goodwill = entity.goodwill,
                investments = entity.investments,
                longTermInvestments = entity.longTermInvestments,
                shortTermInvestments = entity.shortTermInvestments,
                otherCurrentAssets = entity.otherCurrentAssets,
                otherNonCurrentAssets = entity.otherNonCurrentAssets,
                totalLiabilities = entity.totalLiabilities,
                totalCurrentLiabilities = entity.totalCurrentLiabilities,
                currentAccountsPayable = entity.currentAccountsPayable,
                deferredRevenue = entity.deferredRevenue,
                currentDebt = entity.currentDebt,
                shortTermDebt = entity.shortTermDebt,
                totalNonCurrentLiabilities = entity.totalNonCurrentLiabilities,
                capitalLeaseObligations = entity.capitalLeaseObligations,
                longTermDebt = entity.longTermDebt,
                currentLongTermDebt = entity.currentLongTermDebt,
                longTermDebtNoncurrent = entity.longTermDebtNoncurrent,
                shortLongTermDebtTotal = entity.shortLongTermDebtTotal,
                otherCurrentLiabilities = entity.otherCurrentLiabilities,
                otherNonCurrentLiabilities = entity.otherNonCurrentLiabilities,
                totalShareholderEquity = entity.totalShareholderEquity,
                treasuryStock = entity.treasuryStock,
                retainedEarnings = entity.retainedEarnings,
                commonStock = entity.commonStock,
                commonStockSharesOutstanding = entity.commonStockSharesOutstanding,
                isQuarterly = entity.isQuarterly,
                lastUpdated = entity.lastUpdated
            )
        }
    }
}
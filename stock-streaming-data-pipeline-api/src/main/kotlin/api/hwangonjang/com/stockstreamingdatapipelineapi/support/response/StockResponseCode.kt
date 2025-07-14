package api.hwangonjang.com.stockstreamingdatapipelineapi.support.response

class StockResponseCode {
    companion object {
        // Stock Domain Exceptions
        const val STOCK_01 = "Stock-001" // StockNotFoundException
        const val STOCK_02 = "Stock-002" // BalanceSheetNotFoundException
        const val STOCK_03 = "Stock-003" // CashFlowNotFoundException
        const val STOCK_04 = "Stock-004" // CompanyOverviewNotFoundException
        const val STOCK_05 = "Stock-005" // DailyPriceNotFoundException
        const val STOCK_06 = "Stock-006" // IncomeStatementNotFoundException
        const val STOCK_07 = "Stock-007" // NewsNotFoundException
        const val STOCK_08 = "Stock-008" // StockRecommendationNotFoundException
    }
}

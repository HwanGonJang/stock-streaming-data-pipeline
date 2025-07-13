package api.hwangonjang.com.stockstreamingdatapipelineapi.support.enums

enum class StockSymbol(val symbol: String, val companyName: String) {
    AAPL("AAPL", "Apple Inc."),
    MSFT("MSFT", "Microsoft Corporation"),
    GOOGL("GOOGL", "Alphabet Inc."),
    AMZN("AMZN", "Amazon.com Inc."),
    TSLA("TSLA", "Tesla Inc."),
    META("META", "Meta Platforms Inc."),
    NVDA("NVDA", "NVIDIA Corporation"),
    NFLX("NFLX", "Netflix Inc."),
    CRM("CRM", "Salesforce Inc."),
    ORCL("ORCL", "Oracle Corporation"),
    ADBE("ADBE", "Adobe Inc."),
    AMD("AMD", "Advanced Micro Devices Inc."),
    INTC("INTC", "Intel Corporation"),
    PYPL("PYPL", "PayPal Holdings Inc."),
    CSCO("CSCO", "Cisco Systems Inc."),
    QCOM("QCOM", "Qualcomm Incorporated"),
    TXN("TXN", "Texas Instruments Incorporated"),
    AMAT("AMAT", "Applied Materials Inc."),
    PLTR("PLTR", "Palantir Technologies Inc.");

    companion object {
        fun fromSymbol(symbol: String): StockSymbol? {
            return entries.find { it.symbol == symbol.uppercase() }
        }

        fun getAllSymbols(): List<String> {
            return entries.map { it.symbol }
        }

        fun getAllCompanyNames(): List<String> {
            return entries.map { it.companyName }
        }
    }

    override fun toString(): String {
        return symbol
    }
}
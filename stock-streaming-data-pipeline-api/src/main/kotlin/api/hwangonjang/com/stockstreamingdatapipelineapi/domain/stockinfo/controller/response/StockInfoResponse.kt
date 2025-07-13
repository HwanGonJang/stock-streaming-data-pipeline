package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.controller.response

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.dto.StockInfoDto
import java.time.LocalDate
import java.time.LocalDateTime

data class StockInfoResponse(
    val symbol: String,
    val name: String,
    val exchange: String,
    val assetType: String,
    val ipoDate: LocalDate?,
    val delistingDate: LocalDate?,
    val status:  String,
    val lastUpdatedAt: LocalDateTime?,
) {
    companion object {
        fun from(stockInfoDto: StockInfoDto): StockInfoResponse {
            return StockInfoResponse(
                symbol = stockInfoDto.symbol,
                name = stockInfoDto.name,
                exchange = stockInfoDto.exchange,
                assetType = stockInfoDto.assetType,
                ipoDate = stockInfoDto.ipoDate,
                delistingDate = stockInfoDto.delistingDate,
                status = stockInfoDto.status,
                lastUpdatedAt = stockInfoDto.lastUpdatedAt,
            )
        }
    }
}
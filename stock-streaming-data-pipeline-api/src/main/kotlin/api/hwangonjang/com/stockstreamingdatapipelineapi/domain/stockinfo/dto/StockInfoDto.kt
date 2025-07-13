package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.dto

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.entity.StockInfo
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime

data class StockInfoDto(
    val symbol: String,
    val name: String,
    val exchange: String,
    val assetType: String,

    @JsonFormat(pattern = "yyyy-MM-dd")
    val ipoDate: LocalDate? = null,

    @JsonFormat(pattern = "yyyy-MM-dd")
    val delistingDate: LocalDate? = null,

    val status: String,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    val lastUpdatedAt: LocalDateTime? = null
) {
    companion object {
        fun from(entity: StockInfo): StockInfoDto {
            return StockInfoDto(
                symbol = entity.symbol,
                name = entity.name,
                exchange = entity.exchange,
                assetType = entity.assetType,
                ipoDate = entity.ipoDate,
                delistingDate = entity.delistingDate,
                status = entity.status,
                lastUpdatedAt = entity.lastUpdatedAt
            )
        }
    }

    // DTO -> Entity 변환 메서드
    fun toEntity(): StockInfo {
        return StockInfo(
            symbol = this.symbol,
            name = this.name,
            exchange = this.exchange,
            assetType = this.assetType,
            ipoDate = this.ipoDate,
            delistingDate = this.delistingDate,
            status = this.status,
            lastUpdatedAt = this.lastUpdatedAt
        )
    }
}
package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.repository

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.entity.StockInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockInfoRepository : JpaRepository<StockInfo, Long> {
    fun findBySymbol(symbol: String): StockInfo?
}
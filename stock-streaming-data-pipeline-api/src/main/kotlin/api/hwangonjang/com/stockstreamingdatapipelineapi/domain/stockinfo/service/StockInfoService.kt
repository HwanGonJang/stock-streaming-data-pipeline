package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.service

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.dto.StockInfoDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.exception.StockNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stockinfo.repository.StockInfoRepository
import org.springframework.stereotype.Service

@Service
class StockInfoService(
    private val stockInfoRepository: StockInfoRepository,
) {
    fun getStockInfoBySymbol(symbol: String): StockInfoDto {
        val stockInfo = stockInfoRepository.findBySymbol(symbol) ?: throw StockNotFoundException("$symbol not found")

        return StockInfoDto.from(stockInfo)
    }
}

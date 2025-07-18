package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.service

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.dto.TradeDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.repository.TradeRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.*

@Service
class StockStreamingService(
    private val tradeRepository: TradeRepository
) {
    fun streamRealTimeTradeData(
        symbol: String,
        intervalSeconds: Long = 1L,
        useKoreanTimeSimulation: Boolean = false
    ): Flux<TradeDto> {
        return if (useKoreanTimeSimulation) {
            streamKoreanTimeSimulatedData(symbol, intervalSeconds)
        } else {
            streamRealTimeData(symbol, intervalSeconds)
        }
    }

    private fun streamRealTimeData(symbol: String, intervalSeconds: Long): Flux<TradeDto> {
        return Flux.interval(Duration.ofSeconds(intervalSeconds))
            .flatMap {
                val fromTime = LocalDateTime.now().minusSeconds(intervalSeconds)
                tradeRepository.findBySymbolAndTradeTimestampGreaterThanEqual(symbol, fromTime)
                    .map { trade -> TradeDto.from(trade) }
                    .collectList()
                    .flatMapMany { trades ->
                        if (trades.isNotEmpty()) {
                            Flux.fromIterable(trades)
                        } else {
                            // 데이터가 없으면 오늘 날짜의 가장 최근 데이터 1개 반환
                            val today = LocalDate.now()
                            val startOfDay = today.atStartOfDay()
                            val endOfDay = today.atTime(23, 59, 59)
                            
                            tradeRepository.findLatestTradeBySymbolAndDate(symbol, startOfDay, endOfDay)
                                .map { trade -> TradeDto.from(trade) }
                                .switchIfEmpty(Flux.empty())
                        }
                    }
            }
    }

    private fun streamKoreanTimeSimulatedData(symbol: String, intervalSeconds: Long): Flux<TradeDto> {
        return Flux.interval(Duration.ofSeconds(intervalSeconds))
            .filter { isKoreanMarketSimulationTime() }
            .flatMap {
                val now = LocalDateTime.now() // UTC 기준 현재 시간
                val simulatedUsTime = convertKoreanTimeToUsMarketTime(now)
                val fromTime = simulatedUsTime.minusSeconds(intervalSeconds)
                val toTime = simulatedUsTime

                tradeRepository.findBySymbolAndTradeTimestampBetween(symbol, fromTime, toTime)
                    .map { trade ->
                        TradeDto.from(trade).copy(
                            tradeTimestamp = convertToKoreanSimulationTime(trade.primaryKey.tradeTimestamp)
                        )
                    }
                    .collectList()
                    .flatMapMany { trades ->
                        if (trades.isNotEmpty()) {
                            println("🔍 [DEBUG] 조회된 데이터: ${trades.size}건, 시간범위: $fromTime ~ $toTime")
                            Flux.fromIterable(trades)
                        } else {
                            println("⚠️ [DEBUG] 데이터 없음, 시간범위: $fromTime ~ $toTime")
                            // 데이터가 없으면 해당 날짜의 가장 최근 데이터 1개 반환
                            val targetDate = simulatedUsTime.toLocalDate()
                            val startOfDay = targetDate.atStartOfDay()
                            val endOfDay = targetDate.atTime(23, 59, 59)

                            tradeRepository.findLatestTradeBySymbolAndDate(symbol, startOfDay, endOfDay)
                                .map { trade ->
                                    TradeDto.from(trade).copy(
                                        tradeTimestamp = convertToKoreanSimulationTime(trade.primaryKey.tradeTimestamp)
                                    )
                                }
                                .switchIfEmpty(Flux.empty())
                        }
                    }
            }
    }

    /**
     * 현재 UTC 시간을 한국 시간으로 변환 후 미국 장시간으로 매핑
     * 목표: 목요일 미국 장 10:00 => 금요일 한국 10:00에 조회
     */
    private fun convertKoreanTimeToUsMarketTime(currentUtcTime: LocalDateTime): LocalDateTime {
        // 1단계: UTC를 한국 시간으로 변환
        val koreanTime = currentUtcTime.plusHours(9) // UTC + 9시간 = 한국 시간

        // 2단계: 한국 시간을 미국 장시간으로 매핑
        val koreanStartTime = LocalTime.of(4, 0)   // 한국 오전 4시 (프리마켓 시작)
        val koreanEndTime = LocalTime.of(20, 0)    // 한국 오후 8시 (애프터마켓 종료)
        val usStartTime = LocalTime.of(4, 0)       // 미국 오전 4시 (프리마켓)
        val usEndTime = LocalTime.of(20, 0)        // 미국 오후 8시 (애프터마켓)

        val koreanCurrentTime = koreanTime.toLocalTime()

        // 3단계: 1:1 시간 매핑
        val mappedUsTime = when {
            koreanCurrentTime.isBefore(koreanStartTime) -> {
                usEndTime
            }
            koreanCurrentTime.isAfter(koreanEndTime) -> {
                usStartTime
            }
            else -> {
                koreanCurrentTime
            }
        }

        // 4단계: 전날 미국 거래일 계산
        val targetDate = getLastUsMarketDay(koreanTime.toLocalDate())

        // 5단계: 미국 시간을 UTC로 변환 (EDT: UTC-4, EST: UTC-5)
        val usLocalDateTime = LocalDateTime.of(targetDate, mappedUsTime)
        val usUtcTime = convertUsTimeToUtc(usLocalDateTime, targetDate)

        return usUtcTime
    }

    /**
     * 미국 EDT/EST 시간을 UTC로 변환
     */
    private fun convertUsTimeToUtc(usLocalTime: LocalDateTime, date: LocalDate): LocalDateTime {
        val usEasternZone = ZoneId.of("America/New_York")

        // 미국 동부 시간을 ZonedDateTime으로 변환 후 UTC로 변환
        val usZonedTime = usLocalTime.atZone(usEasternZone)
        val utcZonedTime = usZonedTime.withZoneSameInstant(ZoneOffset.UTC)

        return utcZonedTime.toLocalDateTime()
    }

    /**
     * 미국 UTC 시간을 한국 시뮬레이션 시간으로 변환
     */
    private fun convertToKoreanSimulationTime(usUtcTime: LocalDateTime): LocalDateTime {
        // 1단계: UTC 미국 시간을 미국 로컬 시간으로 변환
        val usLocalTime = convertUtcToUsTime(usUtcTime)

        // 2단계: 미국 로컬 시간을 한국 시간으로 1:1 매핑
        val usLocalTimeOnly = usLocalTime.toLocalTime()

        // 3단계: 현재 한국 날짜로 설정
        val koreanToday = LocalDateTime.now().plusHours(9).toLocalDate() // UTC + 9시간 = 한국 날짜

        return LocalDateTime.of(koreanToday, usLocalTimeOnly)
    }

    /**
     * UTC 시간을 미국 로컬 시간으로 변환
     */
    private fun convertUtcToUsTime(utcTime: LocalDateTime): LocalDateTime {
        val usEasternZone = ZoneId.of("America/New_York")

        val utcZonedTime = utcTime.atZone(ZoneOffset.UTC)
        val usZonedTime = utcZonedTime.withZoneSameInstant(usEasternZone)

        return usZonedTime.toLocalDateTime()
    }

    /**
     * 현재 시간이 한국 기준 시뮬레이션 시간인지 확인
     */
    private fun isKoreanMarketSimulationTime(): Boolean {
        return true // 24시간 스트리밍 가능

        // 특정 시간대만 원한다면:
        // val koreanTime = LocalDateTime.now().plusHours(9) // UTC + 9시간 = 한국 시간
        // val currentTime = koreanTime.toLocalTime()
        // val startTime = LocalTime.of(4, 0)   // 오전 4시
        // val endTime = LocalTime.of(20, 0)    // 오후 8시
        // return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime)
    }

    /**
     * 가장 최근 미국 시장 거래일 반환 (주말 제외)
     */
    private fun getLastUsMarketDay(koreanDate: LocalDate): LocalDate {
        var targetDate = koreanDate.minusDays(1) // 전날부터 시작

        // 주말이면 가장 최근 평일로 조정
        while (targetDate.dayOfWeek == DayOfWeek.SATURDAY || targetDate.dayOfWeek == DayOfWeek.SUNDAY) {
            targetDate = targetDate.minusDays(1)
        }

        return targetDate
    }

    fun getLatestTrades(symbol: String, limit: Int = 10): Flux<TradeDto> {
        return tradeRepository.findLatestTradesBySymbol(symbol, limit)
            .map { trade -> TradeDto.from(trade) }
    }
}
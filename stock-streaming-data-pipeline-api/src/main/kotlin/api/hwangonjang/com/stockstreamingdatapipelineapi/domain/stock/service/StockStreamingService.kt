package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.service

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.dto.TradeDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.stock.repository.TradeRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.*
import java.time.format.DateTimeFormatter

@Service
class StockStreamingService(
    private val tradeRepository: TradeRepository
) {
    fun streamRealTimeTradeData(
        symbol: String,
        intervalSeconds: Long = 1L,
        useKoreanTimeSimulation: Boolean = false,
        debugFixedDate: LocalDate? = LocalDate.of(2025, 6, 20)  // 디버깅용 고정 날짜 파라미터
    ): Flux<TradeDto> {
        return if (useKoreanTimeSimulation) {
            if (debugFixedDate != null) {
                streamKoreanTimeSimulatedDataWithFixedDate(symbol, intervalSeconds, debugFixedDate)
            } else {
                streamKoreanTimeSimulatedData(symbol, intervalSeconds)
            }
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
                        if (trades.isNotEmpty()) Flux.fromIterable(trades)
                        else Flux.empty()
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
                        if (trades.isNotEmpty()) Flux.fromIterable(trades)
                        else Flux.empty()
                    }
            }
    }

    private fun streamKoreanTimeSimulatedDataWithFixedDate(
        symbol: String,
        intervalSeconds: Long,
        fixedDate: LocalDate
    ): Flux<TradeDto> {
        return Flux.interval(Duration.ofSeconds(intervalSeconds))
            .filter { isKoreanMarketSimulationTime() }
            .flatMap {
                val now = LocalDateTime.now() // UTC 기준 현재 시간
                val simulatedUsTime = convertKoreanTimeToFixedUsMarketTime(now, fixedDate)
                val fromTime = simulatedUsTime.minusSeconds(intervalSeconds)
                val toTime = simulatedUsTime

                // 디버깅 로그
                logTimeConversionDebug(now, simulatedUsTime, fixedDate)

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
                            Flux.empty()
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

    /**
     * 고정 날짜 기준으로 한국 시간을 미국 UTC 시간으로 변환 (디버깅용)
     */
    private fun convertKoreanTimeToFixedUsMarketTime(
        currentUtcTime: LocalDateTime,
        fixedDate: LocalDate
    ): LocalDateTime {
        // 1단계: UTC를 한국 시간으로 변환
        val koreanTime = currentUtcTime.plusHours(9)
        val koreanCurrentTime = koreanTime.toLocalTime()

        // 2단계: 한국 시간을 미국 시간으로 1:1 매핑
        val koreanStartTime = LocalTime.of(4, 0)
        val koreanEndTime = LocalTime.of(20, 0)
        val usStartTime = LocalTime.of(4, 0)
        val usEndTime = LocalTime.of(20, 0)

        val mappedUsTime = when {
            koreanCurrentTime.isBefore(koreanStartTime) -> usEndTime
            koreanCurrentTime.isAfter(koreanEndTime) -> usStartTime
            else -> koreanCurrentTime
        }

        // 3단계: 고정 날짜의 미국 시간을 UTC로 변환
        val usLocalDateTime = LocalDateTime.of(fixedDate, mappedUsTime)
        val usUtcTime = convertUsTimeToUtc(usLocalDateTime, fixedDate)

        return usUtcTime
    }

    /**
     * 디버깅용 시간 변환 정보 출력
     */
    private fun logTimeConversionDebug(
        currentUtcTime: LocalDateTime,
        usUtcTime: LocalDateTime,
        fixedDate: LocalDate
    ) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val koreanTime = currentUtcTime.plusHours(9)
        val usLocalTime = convertUtcToUsTime(usUtcTime)

        println("🕐 [DEBUG] 고정날짜: $fixedDate")
        println("🌍 [DEBUG] 현재 UTC: ${currentUtcTime.format(formatter)}")
        println("🇰🇷 [DEBUG] 한국시간: ${koreanTime.format(formatter)}")
        println("🇺🇸 [DEBUG] 미국로컬: ${usLocalTime.format(formatter)}")
        println("🌍 [DEBUG] 미국UTC: ${usUtcTime.format(formatter)}")
        println("📊 [DEBUG] 조회범위: ${usUtcTime.minusSeconds(1).format(formatter)} ~ ${usUtcTime.format(formatter)} (UTC)")
        println("----------------------------------------")
    }

    /**
     * 디버깅용 시간 변환 정보 출력
     */
    private fun logTimeConversion(currentUtcTime: LocalDateTime, usUtcTime: LocalDateTime) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val koreanTime = currentUtcTime.plusHours(9)
        val usLocalTime = convertUtcToUsTime(usUtcTime)

        println("🌍 UTC: ${currentUtcTime.format(formatter)} -> 🇰🇷 한국: ${koreanTime.format(formatter)} -> 🇺🇸 미국UTC: ${usUtcTime.format(formatter)}")
    }

    fun getLatestTrades(symbol: String, limit: Int = 10): Flux<TradeDto> {
        return tradeRepository.findLatestTradesBySymbol(symbol, limit)
            .map { trade -> TradeDto.from(trade) }
    }
}
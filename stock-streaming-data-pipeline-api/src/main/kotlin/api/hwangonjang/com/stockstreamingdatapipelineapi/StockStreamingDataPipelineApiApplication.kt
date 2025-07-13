package api.hwangonjang.com.stockstreamingdatapipelineapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class StockStreamingDataPipelineApiApplication

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    System.setProperty("user.timezone", "UTC")

    runApplication<StockStreamingDataPipelineApiApplication>(*args)
}

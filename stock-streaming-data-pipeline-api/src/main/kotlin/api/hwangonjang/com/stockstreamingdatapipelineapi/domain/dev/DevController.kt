package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.dev

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.balancesheet.controller.request.BalanceSheetRequest
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.enums.StockSymbol
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Developer API", description = "개발용 API")
class DevController {
    @GetMapping
    fun ping(): String {
        return "pong"
    }
}
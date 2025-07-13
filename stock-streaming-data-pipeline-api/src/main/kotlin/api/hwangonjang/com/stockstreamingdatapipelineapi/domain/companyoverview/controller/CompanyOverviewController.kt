package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.controller

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.controller.request.CompanyOverviewRequest
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.controller.response.CompanyOverviewResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.companyoverview.service.CompanyOverviewService
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.enums.StockSymbol
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.logging.TraceIdResolver
import api.hwangonjang.com.stockstreamingdatapipelineapi.support.response.StockApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "🏢 Company Overview API", description = "기업 기본 정보 API")
@RequestMapping("/v1/company-overview")
class CompanyOverviewController(
    private val traceIdResolver: TraceIdResolver,
    private val companyOverviewService: CompanyOverviewService
) {
    
    @Operation(
        summary = "Get company overview by symbol",
        description = "특정 종목의 기업 기본 정보 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Not Found")
        ]
    )
    @GetMapping("/{symbol}")
    fun getCompanyOverview(
        @PathVariable symbol: StockSymbol
    ): StockApiResponse<CompanyOverviewResponse> {
        val companyOverview = companyOverviewService.getCompanyOverviewBySymbol(symbol.symbol)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = CompanyOverviewResponse.from(companyOverview)
        )
    }
    
    @Operation(
        summary = "Get companies by filter",
        description = "필터 조건에 따른 기업 정보 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/filter")
    fun getCompaniesByFilter(
        @ModelAttribute request: CompanyOverviewRequest,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<CompanyOverviewResponse>> {
        val companies = when {
            request.sector != null && request.industry != null -> {
                companyOverviewService.getCompanyOverviewsBySectorAndIndustry(
                    request.sector, request.industry, pageable
                )
            }
            request.sector != null -> {
                companyOverviewService.getCompanyOverviewsBySector(request.sector, pageable)
            }
            request.industry != null -> {
                companyOverviewService.getCompanyOverviewsByIndustry(request.industry, pageable)
            }
            request.country != null -> {
                companyOverviewService.getCompanyOverviewsByCountry(request.country, pageable)
            }
            request.minMarketCap != null -> {
                companyOverviewService.getCompanyOverviewsByMarketCap(request.minMarketCap, pageable)
            }
            request.minPE != null && request.maxPE != null -> {
                companyOverviewService.getCompanyOverviewsByPeRatio(
                    request.minPE, request.maxPE, pageable
                )
            }
            request.minDividendYield != null -> {
                companyOverviewService.getCompanyOverviewsByDividendYield(request.minDividendYield, pageable)
            }
            request.minROE != null -> {
                companyOverviewService.getCompanyOverviewsByROE(request.minROE, pageable)
            }
            request.minBeta != null && request.maxBeta != null -> {
                companyOverviewService.getCompanyOverviewsByBeta(
                    request.minBeta, request.maxBeta, pageable
                )
            }
            else -> {
                throw IllegalArgumentException("At least one filter condition must be specified")
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = companies.map { CompanyOverviewResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get top companies by sector",
        description = "섹터별 시가총액 상위 기업 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/top-by-sector")
    fun getTopCompaniesBySector(
        @RequestParam sector: String,
        @PageableDefault(size = 20) pageable: Pageable
    ): StockApiResponse<Page<CompanyOverviewResponse>> {
        val companies = companyOverviewService.getTopCompaniesBySector(sector, pageable)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = companies.map { CompanyOverviewResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get top companies by industry",
        description = "산업별 시가총액 상위 기업 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/top-by-industry")
    fun getTopCompaniesByIndustry(
        @RequestParam industry: String,
        @PageableDefault(size = 20) pageable: Pageable
    ): StockApiResponse<Page<CompanyOverviewResponse>> {
        val companies = companyOverviewService.getTopCompaniesByIndustry(industry, pageable)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = companies.map { CompanyOverviewResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get distinct sectors",
        description = "사용 가능한 섹터 목록 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success")
        ]
    )
    @GetMapping("/sectors")
    fun getDistinctSectors(): StockApiResponse<List<String>> {
        val sectors = companyOverviewService.getDistinctSectors()
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = sectors
        )
    }
    
    @Operation(
        summary = "Get distinct industries",
        description = "사용 가능한 산업 목록 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success")
        ]
    )
    @GetMapping("/industries")
    fun getDistinctIndustries(): StockApiResponse<List<String>> {
        val industries = companyOverviewService.getDistinctIndustries()
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = industries
        )
    }
    
    @Operation(
        summary = "Get distinct countries",
        description = "사용 가능한 국가 목록 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success")
        ]
    )
    @GetMapping("/countries")
    fun getDistinctCountries(): StockApiResponse<List<String>> {
        val countries = companyOverviewService.getDistinctCountries()
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = countries
        )
    }
}
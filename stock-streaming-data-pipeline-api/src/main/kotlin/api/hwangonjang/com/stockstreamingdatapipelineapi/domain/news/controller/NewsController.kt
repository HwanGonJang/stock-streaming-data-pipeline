package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.controller

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.controller.request.NewsRequest
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.controller.response.NewsArticleResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.controller.response.NewsStockResponse
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.service.NewsService
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
import java.math.BigDecimal

@RestController
@Tag(name = "📰 News API", description = "뉴스 관련 API")
@RequestMapping("/v1/news")
class NewsController(
    private val traceIdResolver: TraceIdResolver,
    private val newsService: NewsService
) {
    
    @Operation(
        summary = "Get all news",
        description = "모든 뉴스 조회 (시간순 정렬)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping
    fun getAllNews(
        @ModelAttribute request: NewsRequest,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<NewsArticleResponse>> {
        val news = when {
            request.startDate != null && request.endDate != null -> {
                newsService.getNewsByDateRange(request.startDate, request.endDate, pageable)
            }
            request.source != null -> {
                newsService.getNewsBySource(request.source, pageable)
            }
            request.category != null -> {
                newsService.getNewsByCategory(request.category, pageable)
            }
            request.sourceDomain != null -> {
                newsService.getNewsBySourceDomain(request.sourceDomain, pageable)
            }
            request.sentimentLabel != null -> {
                newsService.getNewsBySentimentLabel(request.sentimentLabel, pageable)
            }
            request.minSentimentScore != null -> {
                newsService.getNewsBySentimentScore(
                    request.minSentimentScore, request.maxSentimentScore, pageable
                )
            }
            request.keyword != null -> {
                newsService.getNewsByKeyword(request.keyword, pageable)
            }
            else -> {
                newsService.getAllNews(pageable)
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = news.map { NewsArticleResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get news by ID",
        description = "특정 뉴스 상세 조회 (연결된 종목 정보 포함)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "404", description = "Not Found")
        ]
    )
    @GetMapping("/{id}")
    fun getNewsById(
        @PathVariable id: Long
    ): StockApiResponse<NewsArticleResponse> {
        val news = newsService.getNewsById(id)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = NewsArticleResponse.from(news)
        )
    }
    
    @Operation(
        summary = "Get news by symbol",
        description = "특정 종목 관련 뉴스 조회 (시간순, 관련도순 정렬)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/symbol/{symbol}")
    fun getNewsBySymbol(
        @PathVariable symbol: StockSymbol,
        @ModelAttribute request: NewsRequest,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<NewsStockResponse>> {
        val news = when {
            request.startDate != null && request.endDate != null -> {
                newsService.getNewsBySymbolAndDateRange(
                    symbol.symbol, request.startDate, request.endDate, pageable
                )
            }
            request.sentimentLabel != null -> {
                newsService.getNewsBySymbolAndSentiment(
                    symbol.symbol, request.sentimentLabel, pageable
                )
            }
            request.minRelevanceScore != null -> {
                newsService.getNewsBySymbolAndRelevance(
                    symbol.symbol, request.minRelevanceScore, pageable
                )
            }
            request.minSentimentScore != null -> {
                newsService.getNewsBySymbolAndSentimentScore(
                    symbol.symbol, request.minSentimentScore, request.maxSentimentScore, pageable
                )
            }
            request.source != null -> {
                newsService.getNewsBySymbolAndSource(
                    symbol.symbol, request.source, pageable
                )
            }
            request.keyword != null -> {
                newsService.getNewsBySymbolAndKeyword(
                    symbol.symbol, request.keyword, pageable
                )
            }
            else -> {
                newsService.getNewsBySymbol(symbol.symbol, pageable)
            }
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = news.map { NewsStockResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get high relevance news",
        description = "높은 관련도의 뉴스 조회 (관련도순, 시간순 정렬)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/high-relevance")
    fun getHighRelevanceNews(
        @RequestParam minRelevance: BigDecimal,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<NewsStockResponse>> {
        val news = newsService.getHighRelevanceNews(minRelevance, pageable)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = news.map { NewsStockResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get news by sentiment",
        description = "감정 라벨별 뉴스 조회 (시간순, 관련도순 정렬)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad Request")
        ]
    )
    @GetMapping("/sentiment/{sentimentLabel}")
    fun getNewsBySentiment(
        @PathVariable sentimentLabel: String,
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<NewsStockResponse>> {
        val news = newsService.getNewsStockBySentimentLabel(sentimentLabel, pageable)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = news.map { NewsStockResponse.from(it) }
        )
    }
    
    @Operation(
        summary = "Get news count by symbol",
        description = "특정 종목의 뉴스 개수 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success")
        ]
    )
    @GetMapping("/count/symbol/{symbol}")
    fun getNewsCountBySymbol(
        @PathVariable symbol: StockSymbol,
        @RequestParam(required = false) sentimentLabel: String?
    ): StockApiResponse<Long> {
        val count = if (sentimentLabel != null) {
            newsService.getNewsCountBySymbolAndSentiment(symbol.symbol, sentimentLabel)
        } else {
            newsService.getNewsCountBySymbol(symbol.symbol)
        }
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = count
        )
    }
    
    @Operation(
        summary = "Get symbol news counts",
        description = "종목별 뉴스 개수 랭킹 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success")
        ]
    )
    @GetMapping("/count/ranking")
    fun getSymbolNewsCounts(
        @PageableDefault(size = 50) pageable: Pageable
    ): StockApiResponse<Page<Array<Any>>> {
        val counts = newsService.getSymbolNewsCounts(pageable)
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = counts
        )
    }
    
    @Operation(
        summary = "Get distinct sources",
        description = "사용 가능한 뉴스 소스 목록 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success")
        ]
    )
    @GetMapping("/sources")
    fun getDistinctSources(): StockApiResponse<List<String>> {
        val sources = newsService.getDistinctSources()
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = sources
        )
    }
    
    @Operation(
        summary = "Get distinct categories",
        description = "사용 가능한 뉴스 카테고리 목록 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success")
        ]
    )
    @GetMapping("/categories")
    fun getDistinctCategories(): StockApiResponse<List<String>> {
        val categories = newsService.getDistinctCategories()
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = categories
        )
    }
    
    @Operation(
        summary = "Get distinct source domains",
        description = "사용 가능한 뉴스 소스 도메인 목록 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success")
        ]
    )
    @GetMapping("/source-domains")
    fun getDistinctSourceDomains(): StockApiResponse<List<String>> {
        val domains = newsService.getDistinctSourceDomains()
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = domains
        )
    }
    
    @Operation(
        summary = "Get distinct sentiment labels",
        description = "사용 가능한 감정 라벨 목록 조회"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success")
        ]
    )
    @GetMapping("/sentiment-labels")
    fun getDistinctSentimentLabels(): StockApiResponse<List<String>> {
        val labels = newsService.getDistinctSentimentLabels()
        
        return StockApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = labels
        )
    }
}
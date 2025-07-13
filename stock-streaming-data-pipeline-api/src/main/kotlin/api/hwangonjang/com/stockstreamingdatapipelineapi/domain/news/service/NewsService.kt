package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.service

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.dto.NewsArticleDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.dto.NewsStockDto
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.exception.NewsNotFoundException
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.repository.NewsArticleRepository
import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.repository.NewsStockRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class NewsService(
    private val newsArticleRepository: NewsArticleRepository,
    private val newsStockRepository: NewsStockRepository
) {
    
    fun getAllNews(pageable: Pageable): Page<NewsArticleDto> {
        return newsArticleRepository.findAllOrderByTimePublishedDesc(pageable)
            .map { NewsArticleDto.from(it) }
    }
    
    fun getNewsByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable
    ): Page<NewsArticleDto> {
        return newsArticleRepository.findByTimePublishedBetweenOrderByTimePublishedDesc(
            startDate, endDate, pageable
        ).map { NewsArticleDto.from(it) }
    }
    
    fun getNewsBySource(source: String, pageable: Pageable): Page<NewsArticleDto> {
        return newsArticleRepository.findBySourceOrderByTimePublishedDesc(source, pageable)
            .map { NewsArticleDto.from(it) }
    }
    
    fun getNewsBySentimentLabel(sentimentLabel: String, pageable: Pageable): Page<NewsArticleDto> {
        return newsArticleRepository.findByOverallSentimentLabelOrderByTimePublishedDesc(
            sentimentLabel, pageable
        ).map { NewsArticleDto.from(it) }
    }
    
    fun getNewsBySentimentScore(
        minScore: BigDecimal,
        maxScore: BigDecimal?,
        pageable: Pageable
    ): Page<NewsArticleDto> {
        return if (maxScore != null) {
            newsArticleRepository.findByOverallSentimentScoreBetweenOrderByTimePublishedDesc(
                minScore, maxScore, pageable
            )
        } else {
            newsArticleRepository.findByOverallSentimentScoreGreaterThanEqualOrderByTimePublishedDesc(
                minScore, pageable
            )
        }.map { NewsArticleDto.from(it) }
    }
    
    fun getNewsByCategory(category: String, pageable: Pageable): Page<NewsArticleDto> {
        return newsArticleRepository.findByCategoryWithinSourceOrderByTimePublishedDesc(
            category, pageable
        ).map { NewsArticleDto.from(it) }
    }
    
    fun getNewsBySourceDomain(domain: String, pageable: Pageable): Page<NewsArticleDto> {
        return newsArticleRepository.findBySourceDomainOrderByTimePublishedDesc(
            domain, pageable
        ).map { NewsArticleDto.from(it) }
    }
    
    fun getNewsByKeyword(keyword: String, pageable: Pageable): Page<NewsArticleDto> {
        return newsArticleRepository.findByTitleContainingOrSummaryContainingOrderByTimePublishedDesc(
            keyword, pageable
        ).map { NewsArticleDto.from(it) }
    }
    
    fun getNewsById(id: Long): NewsArticleDto {
        val newsArticle = newsArticleRepository.findById(id)
            .orElseThrow { NewsNotFoundException("News article not found with id: $id") }
        return NewsArticleDto.from(newsArticle)
    }
    
    fun getNewsBySymbol(symbol: String, pageable: Pageable): Page<NewsStockDto> {
        return newsStockRepository.findBySymbolOrderByTimePublishedDescRelevanceScoreDesc(
            symbol, pageable
        ).map { NewsStockDto.from(it) }
    }
    
    fun getNewsBySymbolAndDateRange(
        symbol: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable
    ): Page<NewsStockDto> {
        return newsStockRepository.findBySymbolAndTimePublishedBetweenOrderByTimePublishedDescRelevanceScoreDesc(
            symbol, startDate, endDate, pageable
        ).map { NewsStockDto.from(it) }
    }
    
    fun getNewsBySymbolAndSentiment(
        symbol: String,
        sentimentLabel: String,
        pageable: Pageable
    ): Page<NewsStockDto> {
        return newsStockRepository.findBySymbolAndSentimentLabelOrderByTimePublishedDescRelevanceScoreDesc(
            symbol, sentimentLabel, pageable
        ).map { NewsStockDto.from(it) }
    }
    
    fun getNewsBySymbolAndRelevance(
        symbol: String,
        minRelevance: BigDecimal,
        pageable: Pageable
    ): Page<NewsStockDto> {
        return newsStockRepository.findBySymbolAndRelevanceScoreGreaterThanEqualOrderByTimePublishedDescRelevanceScoreDesc(
            symbol, minRelevance, pageable
        ).map { NewsStockDto.from(it) }
    }
    
    fun getNewsBySymbolAndSentimentScore(
        symbol: String,
        minSentiment: BigDecimal,
        maxSentiment: BigDecimal?,
        pageable: Pageable
    ): Page<NewsStockDto> {
        return if (maxSentiment != null) {
            newsStockRepository.findBySymbolAndSentimentScoreBetweenOrderByTimePublishedDescRelevanceScoreDesc(
                symbol, minSentiment, maxSentiment, pageable
            )
        } else {
            newsStockRepository.findBySymbolAndSentimentScoreGreaterThanEqualOrderByTimePublishedDescRelevanceScoreDesc(
                symbol, minSentiment, pageable
            )
        }.map { NewsStockDto.from(it) }
    }
    
    fun getNewsBySymbolAndSource(
        symbol: String,
        source: String,
        pageable: Pageable
    ): Page<NewsStockDto> {
        return newsStockRepository.findBySymbolAndSourceOrderByTimePublishedDescRelevanceScoreDesc(
            symbol, source, pageable
        ).map { NewsStockDto.from(it) }
    }
    
    fun getNewsBySymbolAndKeyword(
        symbol: String,
        keyword: String,
        pageable: Pageable
    ): Page<NewsStockDto> {
        return newsStockRepository.findBySymbolAndKeywordOrderByTimePublishedDescRelevanceScoreDesc(
            symbol, keyword, pageable
        ).map { NewsStockDto.from(it) }
    }
    
    fun getHighRelevanceNews(minRelevance: BigDecimal, pageable: Pageable): Page<NewsStockDto> {
        return newsStockRepository.findByRelevanceScoreGreaterThanEqualOrderByRelevanceScoreDescTimePublishedDesc(
            minRelevance, pageable
        ).map { NewsStockDto.from(it) }
    }
    
    fun getNewsStockBySentimentLabel(sentimentLabel: String, pageable: Pageable): Page<NewsStockDto> {
        return newsStockRepository.findBySentimentLabelOrderByTimePublishedDescRelevanceScoreDesc(
            sentimentLabel, pageable
        ).map { NewsStockDto.from(it) }
    }
    
    fun getNewsCountBySymbol(symbol: String): Long {
        return newsStockRepository.countBySymbol(symbol)
    }
    
    fun getNewsCountBySymbolAndSentiment(symbol: String, sentimentLabel: String): Long {
        return newsStockRepository.countBySymbolAndSentimentLabel(symbol, sentimentLabel)
    }
    
    fun getSymbolNewsCounts(pageable: Pageable): Page<Array<Any>> {
        return newsStockRepository.findSymbolNewsCounts(pageable)
    }
    
    fun getDistinctSources(): List<String> {
        return newsArticleRepository.findDistinctSources()
    }
    
    fun getDistinctCategories(): List<String> {
        return newsArticleRepository.findDistinctCategories()
    }
    
    fun getDistinctSourceDomains(): List<String> {
        return newsArticleRepository.findDistinctSourceDomains()
    }
    
    fun getDistinctSentimentLabels(): List<String> {
        return newsArticleRepository.findDistinctSentimentLabels()
    }
}
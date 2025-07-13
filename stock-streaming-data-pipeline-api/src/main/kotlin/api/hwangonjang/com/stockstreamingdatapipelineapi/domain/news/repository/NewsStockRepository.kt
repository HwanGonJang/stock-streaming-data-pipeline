package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.repository

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.entity.NewsStock
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
interface NewsStockRepository : JpaRepository<NewsStock, Long> {
    
    @Query("SELECT ns FROM NewsStock ns WHERE ns.symbol = :symbol ORDER BY ns.newsArticle.timePublished DESC, ns.relevanceScore DESC")
    fun findBySymbolOrderByTimePublishedDescRelevanceScoreDesc(
        @Param("symbol") symbol: String,
        pageable: Pageable
    ): Page<NewsStock>
    
    @Query("SELECT ns FROM NewsStock ns WHERE ns.symbol = :symbol AND ns.newsArticle.timePublished BETWEEN :startDate AND :endDate ORDER BY ns.newsArticle.timePublished DESC, ns.relevanceScore DESC")
    fun findBySymbolAndTimePublishedBetweenOrderByTimePublishedDescRelevanceScoreDesc(
        @Param("symbol") symbol: String,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime,
        pageable: Pageable
    ): Page<NewsStock>
    
    @Query("SELECT ns FROM NewsStock ns WHERE ns.symbol = :symbol AND ns.sentimentLabel = :sentimentLabel ORDER BY ns.newsArticle.timePublished DESC, ns.relevanceScore DESC")
    fun findBySymbolAndSentimentLabelOrderByTimePublishedDescRelevanceScoreDesc(
        @Param("symbol") symbol: String,
        @Param("sentimentLabel") sentimentLabel: String,
        pageable: Pageable
    ): Page<NewsStock>
    
    @Query("SELECT ns FROM NewsStock ns WHERE ns.symbol = :symbol AND ns.relevanceScore >= :minRelevance ORDER BY ns.newsArticle.timePublished DESC, ns.relevanceScore DESC")
    fun findBySymbolAndRelevanceScoreGreaterThanEqualOrderByTimePublishedDescRelevanceScoreDesc(
        @Param("symbol") symbol: String,
        @Param("minRelevance") minRelevance: BigDecimal,
        pageable: Pageable
    ): Page<NewsStock>
    
    @Query("SELECT ns FROM NewsStock ns WHERE ns.symbol = :symbol AND ns.sentimentScore >= :minSentiment ORDER BY ns.newsArticle.timePublished DESC, ns.relevanceScore DESC")
    fun findBySymbolAndSentimentScoreGreaterThanEqualOrderByTimePublishedDescRelevanceScoreDesc(
        @Param("symbol") symbol: String,
        @Param("minSentiment") minSentiment: BigDecimal,
        pageable: Pageable
    ): Page<NewsStock>
    
    @Query("SELECT ns FROM NewsStock ns WHERE ns.symbol = :symbol AND ns.sentimentScore BETWEEN :minSentiment AND :maxSentiment ORDER BY ns.newsArticle.timePublished DESC, ns.relevanceScore DESC")
    fun findBySymbolAndSentimentScoreBetweenOrderByTimePublishedDescRelevanceScoreDesc(
        @Param("symbol") symbol: String,
        @Param("minSentiment") minSentiment: BigDecimal,
        @Param("maxSentiment") maxSentiment: BigDecimal,
        pageable: Pageable
    ): Page<NewsStock>
    
    @Query("SELECT ns FROM NewsStock ns WHERE ns.symbol = :symbol AND ns.newsArticle.source = :source ORDER BY ns.newsArticle.timePublished DESC, ns.relevanceScore DESC")
    fun findBySymbolAndSourceOrderByTimePublishedDescRelevanceScoreDesc(
        @Param("symbol") symbol: String,
        @Param("source") source: String,
        pageable: Pageable
    ): Page<NewsStock>
    
    @Query("SELECT ns FROM NewsStock ns WHERE ns.symbol = :symbol AND (ns.newsArticle.title LIKE %:keyword% OR ns.newsArticle.summary LIKE %:keyword%) ORDER BY ns.newsArticle.timePublished DESC, ns.relevanceScore DESC")
    fun findBySymbolAndKeywordOrderByTimePublishedDescRelevanceScoreDesc(
        @Param("symbol") symbol: String,
        @Param("keyword") keyword: String,
        pageable: Pageable
    ): Page<NewsStock>
    
    @Query("SELECT ns FROM NewsStock ns ORDER BY ns.relevanceScore DESC, ns.newsArticle.timePublished DESC")
    fun findAllOrderByRelevanceScoreDescTimePublishedDesc(pageable: Pageable): Page<NewsStock>
    
    @Query("SELECT ns FROM NewsStock ns WHERE ns.relevanceScore >= :minRelevance ORDER BY ns.relevanceScore DESC, ns.newsArticle.timePublished DESC")
    fun findByRelevanceScoreGreaterThanEqualOrderByRelevanceScoreDescTimePublishedDesc(
        @Param("minRelevance") minRelevance: BigDecimal,
        pageable: Pageable
    ): Page<NewsStock>
    
    @Query("SELECT ns FROM NewsStock ns WHERE ns.sentimentLabel = :sentimentLabel ORDER BY ns.newsArticle.timePublished DESC, ns.relevanceScore DESC")
    fun findBySentimentLabelOrderByTimePublishedDescRelevanceScoreDesc(
        @Param("sentimentLabel") sentimentLabel: String,
        pageable: Pageable
    ): Page<NewsStock>
    
    @Query("SELECT COUNT(ns) FROM NewsStock ns WHERE ns.symbol = :symbol")
    fun countBySymbol(@Param("symbol") symbol: String): Long
    
    @Query("SELECT COUNT(ns) FROM NewsStock ns WHERE ns.symbol = :symbol AND ns.sentimentLabel = :sentimentLabel")
    fun countBySymbolAndSentimentLabel(
        @Param("symbol") symbol: String,
        @Param("sentimentLabel") sentimentLabel: String
    ): Long
    
    @Query("SELECT ns.symbol, COUNT(ns) FROM NewsStock ns GROUP BY ns.symbol ORDER BY COUNT(ns) DESC")
    fun findSymbolNewsCounts(pageable: Pageable): Page<Array<Any>>
}
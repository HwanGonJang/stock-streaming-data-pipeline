package api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.repository

import api.hwangonjang.com.stockstreamingdatapipelineapi.domain.news.entity.NewsArticle
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
interface NewsArticleRepository : JpaRepository<NewsArticle, Long> {
    
    @Query("SELECT n FROM NewsArticle n ORDER BY n.timePublished DESC")
    fun findAllOrderByTimePublishedDesc(pageable: Pageable): Page<NewsArticle>
    
    @Query("SELECT n FROM NewsArticle n WHERE n.timePublished BETWEEN :startDate AND :endDate ORDER BY n.timePublished DESC")
    fun findByTimePublishedBetweenOrderByTimePublishedDesc(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime,
        pageable: Pageable
    ): Page<NewsArticle>
    
    @Query("SELECT n FROM NewsArticle n WHERE n.source = :source ORDER BY n.timePublished DESC")
    fun findBySourceOrderByTimePublishedDesc(
        @Param("source") source: String,
        pageable: Pageable
    ): Page<NewsArticle>
    
    @Query("SELECT n FROM NewsArticle n WHERE n.overallSentimentLabel = :sentimentLabel ORDER BY n.timePublished DESC")
    fun findByOverallSentimentLabelOrderByTimePublishedDesc(
        @Param("sentimentLabel") sentimentLabel: String,
        pageable: Pageable
    ): Page<NewsArticle>
    
    @Query("SELECT n FROM NewsArticle n WHERE n.overallSentimentScore >= :minScore ORDER BY n.timePublished DESC")
    fun findByOverallSentimentScoreGreaterThanEqualOrderByTimePublishedDesc(
        @Param("minScore") minScore: BigDecimal,
        pageable: Pageable
    ): Page<NewsArticle>
    
    @Query("SELECT n FROM NewsArticle n WHERE n.overallSentimentScore BETWEEN :minScore AND :maxScore ORDER BY n.timePublished DESC")
    fun findByOverallSentimentScoreBetweenOrderByTimePublishedDesc(
        @Param("minScore") minScore: BigDecimal,
        @Param("maxScore") maxScore: BigDecimal,
        pageable: Pageable
    ): Page<NewsArticle>
    
    @Query("SELECT n FROM NewsArticle n WHERE n.categoryWithinSource = :category ORDER BY n.timePublished DESC")
    fun findByCategoryWithinSourceOrderByTimePublishedDesc(
        @Param("category") category: String,
        pageable: Pageable
    ): Page<NewsArticle>
    
    @Query("SELECT n FROM NewsArticle n WHERE n.sourceDomain = :domain ORDER BY n.timePublished DESC")
    fun findBySourceDomainOrderByTimePublishedDesc(
        @Param("domain") domain: String,
        pageable: Pageable
    ): Page<NewsArticle>
    
    @Query("SELECT n FROM NewsArticle n WHERE n.title LIKE %:keyword% OR n.summary LIKE %:keyword% ORDER BY n.timePublished DESC")
    fun findByTitleContainingOrSummaryContainingOrderByTimePublishedDesc(
        @Param("keyword") keyword: String,
        pageable: Pageable
    ): Page<NewsArticle>
    
    @Query("SELECT DISTINCT n.source FROM NewsArticle n WHERE n.source IS NOT NULL ORDER BY n.source")
    fun findDistinctSources(): List<String>
    
    @Query("SELECT DISTINCT n.categoryWithinSource FROM NewsArticle n WHERE n.categoryWithinSource IS NOT NULL ORDER BY n.categoryWithinSource")
    fun findDistinctCategories(): List<String>
    
    @Query("SELECT DISTINCT n.sourceDomain FROM NewsArticle n WHERE n.sourceDomain IS NOT NULL ORDER BY n.sourceDomain")
    fun findDistinctSourceDomains(): List<String>
    
    @Query("SELECT DISTINCT n.overallSentimentLabel FROM NewsArticle n WHERE n.overallSentimentLabel IS NOT NULL ORDER BY n.overallSentimentLabel")
    fun findDistinctSentimentLabels(): List<String>
}
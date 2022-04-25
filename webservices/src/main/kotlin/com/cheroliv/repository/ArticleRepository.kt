package com.cheroliv.repository

import com.cheroliv.domain.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [Article] entity.
 */
@Suppress("unused")
@Repository
interface ArticleRepository : JpaRepository<Article, Long>

package com.cheroliv.domain

import com.cheroliv.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ArticleTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Article::class)
        val article1 = Article()
        article1.id = 1L
        val article2 = Article()
        article2.id = article1.id
        assertThat(article1).isEqualTo(article2)
        article2.id = 2L
        assertThat(article1).isNotEqualTo(article2)
        article1.id = null
        assertThat(article1).isNotEqualTo(article2)
    }
}

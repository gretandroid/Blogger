package com.cheroliv.web.rest

import com.cheroliv.IntegrationTest
import com.cheroliv.domain.Article
import com.cheroliv.domain.Person
import com.cheroliv.repository.ArticleRepository
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.util.Random
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [ArticleResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ArticleResourceIT {
    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restArticleMockMvc: MockMvc

    private lateinit var article: Article

    @BeforeEach
    fun initTest() {
        article = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createArticle() {
        val databaseSizeBeforeCreate = articleRepository.findAll().size
        // Create the Article
        restArticleMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(article))
        ).andExpect(status().isCreated)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeCreate + 1)
        val testArticle = articleList[articleList.size - 1]

        assertThat(testArticle.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testArticle.content).isEqualTo(DEFAULT_CONTENT)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createArticleWithExistingId() {
        // Create the Article with an existing ID
        article.id = 1L

        val databaseSizeBeforeCreate = articleRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restArticleMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(article))
        ).andExpect(status().isBadRequest)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllArticles() {
        // Initialize the database
        articleRepository.saveAndFlush(article)

        // Get all the articleList
        restArticleMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(article.id?.toInt())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getArticle() {
        // Initialize the database
        articleRepository.saveAndFlush(article)

        val id = article.id
        assertNotNull(id)

        // Get the article
        restArticleMockMvc.perform(get(ENTITY_API_URL_ID, article.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(article.id?.toInt()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingArticle() {
        // Get the article
        restArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewArticle() {
        // Initialize the database
        articleRepository.saveAndFlush(article)

        val databaseSizeBeforeUpdate = articleRepository.findAll().size

        // Update the article
        val updatedArticle = articleRepository.findById(article.id).get()
        // Disconnect from session so that the updates on updatedArticle are not directly saved in db
        em.detach(updatedArticle)
        updatedArticle.title = UPDATED_TITLE
        updatedArticle.content = UPDATED_CONTENT

        restArticleMockMvc.perform(
            put(ENTITY_API_URL_ID, updatedArticle.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedArticle))
        ).andExpect(status().isOk)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
        val testArticle = articleList[articleList.size - 1]
        assertThat(testArticle.title).isEqualTo(UPDATED_TITLE)
        assertThat(testArticle.content).isEqualTo(UPDATED_CONTENT)
    }

    @Test
    @Transactional
    fun putNonExistingArticle() {
        val databaseSizeBeforeUpdate = articleRepository.findAll().size
        article.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleMockMvc.perform(
            put(ENTITY_API_URL_ID, article.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(article))
        )
            .andExpect(status().isBadRequest)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchArticle() {
        val databaseSizeBeforeUpdate = articleRepository.findAll().size
        article.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(article))
        ).andExpect(status().isBadRequest)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamArticle() {
        val databaseSizeBeforeUpdate = articleRepository.findAll().size
        article.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(article))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateArticleWithPatch() {
        articleRepository.saveAndFlush(article)

        val databaseSizeBeforeUpdate = articleRepository.findAll().size

// Update the article using partial update
        val partialUpdatedArticle = Article().apply {
            id = article.id

            content = UPDATED_CONTENT
        }

        restArticleMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedArticle.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedArticle))
        )
            .andExpect(status().isOk)

// Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
        val testArticle = articleList.last()
        assertThat(testArticle.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testArticle.content).isEqualTo(UPDATED_CONTENT)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateArticleWithPatch() {
        articleRepository.saveAndFlush(article)

        val databaseSizeBeforeUpdate = articleRepository.findAll().size

// Update the article using partial update
        val partialUpdatedArticle = Article().apply {
            id = article.id

            title = UPDATED_TITLE
            content = UPDATED_CONTENT
        }

        restArticleMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedArticle.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedArticle))
        )
            .andExpect(status().isOk)

// Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
        val testArticle = articleList.last()
        assertThat(testArticle.title).isEqualTo(UPDATED_TITLE)
        assertThat(testArticle.content).isEqualTo(UPDATED_CONTENT)
    }

    @Throws(Exception::class)
    fun patchNonExistingArticle() {
        val databaseSizeBeforeUpdate = articleRepository.findAll().size
        article.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleMockMvc.perform(
            patch(ENTITY_API_URL_ID, article.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(article))
        )
            .andExpect(status().isBadRequest)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchArticle() {
        val databaseSizeBeforeUpdate = articleRepository.findAll().size
        article.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(article))
        )
            .andExpect(status().isBadRequest)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamArticle() {
        val databaseSizeBeforeUpdate = articleRepository.findAll().size
        article.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(article))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Article in the database
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteArticle() {
        // Initialize the database
        articleRepository.saveAndFlush(article)

        val databaseSizeBeforeDelete = articleRepository.findAll().size

        // Delete the article
        restArticleMockMvc.perform(
            delete(ENTITY_API_URL_ID, article.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val articleList = articleRepository.findAll()
        assertThat(articleList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private const val DEFAULT_CONTENT = "AAAAAAAAAA"
        private const val UPDATED_CONTENT = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/articles"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"

        private val random: Random = Random()
        private val count: AtomicLong = AtomicLong(random.nextInt().toLong() + (2 * Integer.MAX_VALUE))

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Article {
            val article = Article(
                title = DEFAULT_TITLE,

                content = DEFAULT_CONTENT

            )

            // Add required entity
            val person: Person
            if (findAll(em, Person::class).isEmpty()) {
                person = PersonResourceIT.createEntity(em)
                em.persist(person)
                em.flush()
            } else {
                person = findAll(em, Person::class)[0]
            }
            article.person = person
            return article
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Article {
            val article = Article(
                title = UPDATED_TITLE,

                content = UPDATED_CONTENT

            )

            // Add required entity
            val person: Person
            if (findAll(em, Person::class).isEmpty()) {
                person = PersonResourceIT.createUpdatedEntity(em)
                em.persist(person)
                em.flush()
            } else {
                person = findAll(em, Person::class)[0]
            }
            article.person = person
            return article
        }
    }
}

package com.cheroliv.web.rest

import com.cheroliv.IntegrationTest
import com.cheroliv.domain.Person
import com.cheroliv.repository.PersonRepository
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
 * Integration tests for the [PersonResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PersonResourceIT {
    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restPersonMockMvc: MockMvc

    private lateinit var person: Person

    @BeforeEach
    fun initTest() {
        person = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createPerson() {
        val databaseSizeBeforeCreate = personRepository.findAll().size
        // Create the Person
        restPersonMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(person))
        ).andExpect(status().isCreated)

        // Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeCreate + 1)
        val testPerson = personList[personList.size - 1]

        assertThat(testPerson.name).isEqualTo(DEFAULT_NAME)
        assertThat(testPerson.username).isEqualTo(DEFAULT_USERNAME)
        assertThat(testPerson.email).isEqualTo(DEFAULT_EMAIL)
        assertThat(testPerson.company).isEqualTo(DEFAULT_COMPANY)
        assertThat(testPerson.website).isEqualTo(DEFAULT_WEBSITE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createPersonWithExistingId() {
        // Create the Person with an existing ID
        person.id = 1L

        val databaseSizeBeforeCreate = personRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(person))
        ).andExpect(status().isBadRequest)

        // Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllPeople() {
        // Initialize the database
        personRepository.saveAndFlush(person)

        // Get all the personList
        restPersonMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(person.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY)))
            .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getPerson() {
        // Initialize the database
        personRepository.saveAndFlush(person)

        val id = person.id
        assertNotNull(id)

        // Get the person
        restPersonMockMvc.perform(get(ENTITY_API_URL_ID, person.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(person.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.company").value(DEFAULT_COMPANY))
            .andExpect(jsonPath("$.website").value(DEFAULT_WEBSITE))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingPerson() {
        // Get the person
        restPersonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewPerson() {
        // Initialize the database
        personRepository.saveAndFlush(person)

        val databaseSizeBeforeUpdate = personRepository.findAll().size

        // Update the person
        val updatedPerson = personRepository.findById(person.id).get()
        // Disconnect from session so that the updates on updatedPerson are not directly saved in db
        em.detach(updatedPerson)
        updatedPerson.name = UPDATED_NAME
        updatedPerson.username = UPDATED_USERNAME
        updatedPerson.email = UPDATED_EMAIL
        updatedPerson.company = UPDATED_COMPANY
        updatedPerson.website = UPDATED_WEBSITE

        restPersonMockMvc.perform(
            put(ENTITY_API_URL_ID, updatedPerson.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedPerson))
        ).andExpect(status().isOk)

        // Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeUpdate)
        val testPerson = personList[personList.size - 1]
        assertThat(testPerson.name).isEqualTo(UPDATED_NAME)
        assertThat(testPerson.username).isEqualTo(UPDATED_USERNAME)
        assertThat(testPerson.email).isEqualTo(UPDATED_EMAIL)
        assertThat(testPerson.company).isEqualTo(UPDATED_COMPANY)
        assertThat(testPerson.website).isEqualTo(UPDATED_WEBSITE)
    }

    @Test
    @Transactional
    fun putNonExistingPerson() {
        val databaseSizeBeforeUpdate = personRepository.findAll().size
        person.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonMockMvc.perform(
            put(ENTITY_API_URL_ID, person.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(person))
        )
            .andExpect(status().isBadRequest)

        // Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchPerson() {
        val databaseSizeBeforeUpdate = personRepository.findAll().size
        person.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(person))
        ).andExpect(status().isBadRequest)

        // Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamPerson() {
        val databaseSizeBeforeUpdate = personRepository.findAll().size
        person.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(person))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdatePersonWithPatch() {
        personRepository.saveAndFlush(person)

        val databaseSizeBeforeUpdate = personRepository.findAll().size

// Update the person using partial update
        val partialUpdatedPerson = Person().apply {
            id = person.id

            name = UPDATED_NAME
            email = UPDATED_EMAIL
        }

        restPersonMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedPerson.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedPerson))
        )
            .andExpect(status().isOk)

// Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeUpdate)
        val testPerson = personList.last()
        assertThat(testPerson.name).isEqualTo(UPDATED_NAME)
        assertThat(testPerson.username).isEqualTo(DEFAULT_USERNAME)
        assertThat(testPerson.email).isEqualTo(UPDATED_EMAIL)
        assertThat(testPerson.company).isEqualTo(DEFAULT_COMPANY)
        assertThat(testPerson.website).isEqualTo(DEFAULT_WEBSITE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdatePersonWithPatch() {
        personRepository.saveAndFlush(person)

        val databaseSizeBeforeUpdate = personRepository.findAll().size

// Update the person using partial update
        val partialUpdatedPerson = Person().apply {
            id = person.id

            name = UPDATED_NAME
            username = UPDATED_USERNAME
            email = UPDATED_EMAIL
            company = UPDATED_COMPANY
            website = UPDATED_WEBSITE
        }

        restPersonMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedPerson.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedPerson))
        )
            .andExpect(status().isOk)

// Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeUpdate)
        val testPerson = personList.last()
        assertThat(testPerson.name).isEqualTo(UPDATED_NAME)
        assertThat(testPerson.username).isEqualTo(UPDATED_USERNAME)
        assertThat(testPerson.email).isEqualTo(UPDATED_EMAIL)
        assertThat(testPerson.company).isEqualTo(UPDATED_COMPANY)
        assertThat(testPerson.website).isEqualTo(UPDATED_WEBSITE)
    }

    @Throws(Exception::class)
    fun patchNonExistingPerson() {
        val databaseSizeBeforeUpdate = personRepository.findAll().size
        person.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonMockMvc.perform(
            patch(ENTITY_API_URL_ID, person.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(person))
        )
            .andExpect(status().isBadRequest)

        // Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchPerson() {
        val databaseSizeBeforeUpdate = personRepository.findAll().size
        person.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(person))
        )
            .andExpect(status().isBadRequest)

        // Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamPerson() {
        val databaseSizeBeforeUpdate = personRepository.findAll().size
        person.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(person))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Person in the database
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deletePerson() {
        // Initialize the database
        personRepository.saveAndFlush(person)

        val databaseSizeBeforeDelete = personRepository.findAll().size

        // Delete the person
        restPersonMockMvc.perform(
            delete(ENTITY_API_URL_ID, person.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val personList = personRepository.findAll()
        assertThat(personList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_USERNAME = "AAAAAAAAAA"
        private const val UPDATED_USERNAME = "BBBBBBBBBB"

        private const val DEFAULT_EMAIL = "AAAAAAAAAA"
        private const val UPDATED_EMAIL = "BBBBBBBBBB"

        private const val DEFAULT_COMPANY = "AAAAAAAAAA"
        private const val UPDATED_COMPANY = "BBBBBBBBBB"

        private const val DEFAULT_WEBSITE = "AAAAAAAAAA"
        private const val UPDATED_WEBSITE = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/people"
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
        fun createEntity(em: EntityManager): Person {
            val person = Person(
                name = DEFAULT_NAME,

                username = DEFAULT_USERNAME,

                email = DEFAULT_EMAIL,

                company = DEFAULT_COMPANY,

                website = DEFAULT_WEBSITE

            )

            return person
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Person {
            val person = Person(
                name = UPDATED_NAME,

                username = UPDATED_USERNAME,

                email = UPDATED_EMAIL,

                company = UPDATED_COMPANY,

                website = UPDATED_WEBSITE

            )

            return person
        }
    }
}

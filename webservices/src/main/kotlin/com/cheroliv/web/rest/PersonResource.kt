package com.cheroliv.web.rest

import com.cheroliv.domain.Person
import com.cheroliv.repository.PersonRepository
import com.cheroliv.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.PaginationUtil
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects

private const val ENTITY_NAME = "person"
/**
 * REST controller for managing [com.cheroliv.domain.Person].
 */
@RestController
@RequestMapping("/api")
@Transactional
class PersonResource(
    private val personRepository: PersonRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "person"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /people` : Create a new person.
     *
     * @param person the person to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new person, or with status `400 (Bad Request)` if the person has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/people")
    fun createPerson(@RequestBody person: Person): ResponseEntity<Person> {
        log.debug("REST request to save Person : $person")
        if (person.id != null) {
            throw BadRequestAlertException(
                "A new person cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = personRepository.save(person)
        return ResponseEntity.created(URI("/api/people/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /people/:id} : Updates an existing person.
     *
     * @param id the id of the person to save.
     * @param person the person to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated person,
     * or with status `400 (Bad Request)` if the person is not valid,
     * or with status `500 (Internal Server Error)` if the person couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/people/{id}")
    fun updatePerson(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody person: Person
    ): ResponseEntity<Person> {
        log.debug("REST request to update Person : {}, {}", id, person)
        if (person.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, person.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!personRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = personRepository.save(person)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                    person.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /people/:id} : Partial updates given fields of an existing person, field will ignore if it is null
     *
     * @param id the id of the person to save.
     * @param person the person to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated person,
     * or with status {@code 400 (Bad Request)} if the person is not valid,
     * or with status {@code 404 (Not Found)} if the person is not found,
     * or with status {@code 500 (Internal Server Error)} if the person couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/people/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdatePerson(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody person: Person
    ): ResponseEntity<Person> {
        log.debug("REST request to partial update Person partially : {}, {}", id, person)
        if (person.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, person.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!personRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = personRepository.findById(person.id)
            .map {

                if (person.name != null) {
                    it.name = person.name
                }
                if (person.username != null) {
                    it.username = person.username
                }
                if (person.email != null) {
                    it.email = person.email
                }
                if (person.company != null) {
                    it.company = person.company
                }
                if (person.website != null) {
                    it.website = person.website
                }

                it
            }
            .map { personRepository.save(it) }

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, person.id.toString())
        )
    }

    /**
     * `GET  /people` : get all the people.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of people in body.
     */
    @GetMapping("/people")
    fun getAllPeople(@org.springdoc.api.annotations.ParameterObject pageable: Pageable): ResponseEntity<List<Person>> {

        log.debug("REST request to get a page of People")
        val page = personRepository.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /people/:id` : get the "id" person.
     *
     * @param id the id of the person to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the person, or with status `404 (Not Found)`.
     */
    @GetMapping("/people/{id}")
    fun getPerson(@PathVariable id: Long): ResponseEntity<Person> {
        log.debug("REST request to get Person : $id")
        val person = personRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(person)
    }
    /**
     *  `DELETE  /people/:id` : delete the "id" person.
     *
     * @param id the id of the person to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/people/{id}")
    fun deletePerson(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Person : $id")

        personRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}

package com.cheroliv.repository

import com.cheroliv.domain.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [Person] entity.
 */
@Suppress("unused")
@Repository
interface PersonRepository : JpaRepository<Person, Long>

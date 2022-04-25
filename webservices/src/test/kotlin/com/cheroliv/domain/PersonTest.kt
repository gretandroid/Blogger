package com.cheroliv.domain

import com.cheroliv.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PersonTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Person::class)
        val person1 = Person()
        person1.id = 1L
        val person2 = Person()
        person2.id = person1.id
        assertThat(person1).isEqualTo(person2)
        person2.id = 2L
        assertThat(person1).isNotEqualTo(person2)
        person1.id = null
        assertThat(person1).isNotEqualTo(person2)
    }
}

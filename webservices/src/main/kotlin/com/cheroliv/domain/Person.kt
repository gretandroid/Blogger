package com.cheroliv.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

/**
 * A Person.
 */

@Entity
@Table(name = "person")

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Person(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "username")
    var username: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "company")
    var company: String? = null,

    @Column(name = "website")
    var website: String? = null,
    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false
        return id != null && other.id != null && id == other.id
    }

    @Override
    override fun toString(): String {
        return "Person{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", username='" + username + "'" +
            ", email='" + email + "'" +
            ", company='" + company + "'" +
            ", website='" + website + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}

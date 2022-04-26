package com.cheroliv.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Article.
 */

@Entity
@Table(name = "article")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Article(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "title")
    var title: String? = null,

    @Column(name = "content")
    var content: String? = null,

    @ManyToOne(optional = false)
    @NotNull
    var person: Person? = null,
    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun person(person: Person?): Article {
        this.person = person
        return this
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Article) return false
        return id != null && other.id != null && id == other.id
    }

    @Override
    override fun toString(): String {
        return "Article{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", content='" + content + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}

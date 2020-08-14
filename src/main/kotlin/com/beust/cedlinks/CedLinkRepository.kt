package com.beust.cedlinks

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.transaction.Transactional

@Entity(name = "links")
class LinkFromDb : PanacheEntityBase {
    @Id
    @SequenceGenerator(
            name = "LinksSequence",
            sequenceName = "links_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LinksSequence")
    var id: Long? = 0

    lateinit var url: String
    lateinit var title: String
    var comment: String? = null
    var imageUrl: String? = null
    var saved: String? = null
    var published: String? = null
}

@ApplicationScoped
class CedLinkRepository: PanacheRepository<LinkFromDb> {
    private val log = Logger.getLogger(CedLinkRepository::class.java)

    fun listLinks(all: Boolean, id: Long?): List<LinkFromDb> {
        if (all) {
            return listAll()
        } else if (id != null) {
            val result = findById(id)
            return if (result != null) listOf(result)
                else emptyList()
        } else {
            return find("published != null").list()
        }
    }

    @Transactional
    fun insertLink(url: String, title: String, comment: String, imageUrl: String?) {
        val link = LinkFromDb().apply {
            this.url = url
            this.title = title
            this.comment = comment
            this.imageUrl = imageUrl
        }
        link.persist()
        log.info("Persisted $link id:${link.id}")
    }
}

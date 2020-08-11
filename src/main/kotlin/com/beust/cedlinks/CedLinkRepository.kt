package com.beust.cedlinks

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "links")
class LinkFromDb {
    @Id
    var id: Long = 0

    lateinit var url: String
    lateinit var title: String
    var comment: String? = null
    var imageUrl: String? = null
    var saved: String? = null
    var published: String? = null
}

@ApplicationScoped
class CedLinkRepository: PanacheRepository<LinkFromDb> {

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
}

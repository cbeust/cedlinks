package com.beust.cedlinks

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity
import javax.persistence.Id

open class Link {
    open lateinit var url: String
    open lateinit var title: String
    open var comment: String? = null
    open var imageUrl: String? = null
}

@Entity(name = "links")
class LinkFromDb: Link() {
    @Id
    var id: Int = 0

    override lateinit var url: String
    override lateinit var title: String
    override var comment: String? = null
    override var imageUrl: String? = null

    var saved: String? = null
    var published: String? = null
}

@ApplicationScoped
class CedLinkRepository: PanacheRepository<LinkFromDb> {

    fun listLinks(all: Boolean = true): List<LinkFromDb> {
//        if (all) {
//            return listOf(QLink("https://reddit.com"))
            return listAll()
//        } else {
//            TODO("Not implemented yet")
//        }
    }
}

package com.beust.cedlinks

import javax.inject.Inject
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Entity

@Entity
data class QLink(val url: String = ""): PanacheEntity()

@ApplicationScoped
class CedLinkRepository: PanacheRepository<QLink> {

    fun listLinks(all: Boolean): List<QLink> {
        if (all) {
            return listAll()
        } else {
            TODO("Not implemented yet")
        }
    }
}

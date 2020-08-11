package com.beust.cedlinks

import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CedLinkResource {
    @Inject
    @field: Default
    lateinit var repository: CedLinkRepository

    @GET
    @Path("/list")
    @Transactional
    fun list(@QueryParam("all") all: Boolean = false): List<LinkFromDb> {
//        repository.persist(QLink("https://reddit.com"))
        return repository.listLinks(all)
    }
}
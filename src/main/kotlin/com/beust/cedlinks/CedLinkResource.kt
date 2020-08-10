package com.beust.cedlinks

import javax.enterprise.inject.Default
import javax.inject.Inject
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
    fun list(@QueryParam("all") all: Boolean = false) = repository.listLinks(all)
}
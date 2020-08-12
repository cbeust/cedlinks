package com.beust.cedlinks

import org.jboss.resteasy.annotations.Form
import java.net.URI
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CedLinkResource: BaseController() {
    @Inject
    @field: Default
    lateinit var repository: CedLinkRepository

    @GET
    @Path("list")
    @Transactional
    fun list(@QueryParam("all") all: Boolean = false,
            @QueryParam("id") id: Long? = null): List<LinkFromDb>
    {
        return repository.listLinks(all, id)
    }

    @GET
    @Path("submit")
    @Produces(MediaType.TEXT_HTML)
    fun submitLink(
            @QueryParam("url") url: String = "",
            @QueryParam("title") title: String? = null,
            @QueryParam("comment") comment: String? = null,
            @QueryParam("time") time: String? = null
    ): String
    {
        val result = Template.render("submitLink.mustache", mapOf(
                "url" to url,
                "comment" to comment,
                "title" to title,
                "host" to Config.host,
                "time" to time
        ))
        return result
    }

    class FormLink {
        @FormParam("url")
        lateinit var url: String
        @FormParam("title")
        lateinit var title: String
        @FormParam("comment")
        lateinit var comment: String
        @FormParam("imageUrl")
        var imageUrl: String? = null
        @FormParam("time")
        lateinit var time: String
    }

    @POST
    @Path("insertLink")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    fun insertLink(@Form link: FormLink) =
        timeRequest(link.time) {
            repository.insertLink(link.url, link.title, link.comment, link.imageUrl)
            Response.seeOther(URI(link.url))
        }
//
//    @Get("preview")
//    @Produces(MediaType.TEXT_HTML)
//    fun publish(): String = dao.preview()

}

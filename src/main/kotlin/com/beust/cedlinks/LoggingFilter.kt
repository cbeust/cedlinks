package com.beust.cedlinks

import io.vertx.core.http.HttpServerRequest
import org.jboss.logging.Logger
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.core.Context
import javax.ws.rs.core.UriInfo
import javax.ws.rs.ext.Provider

@Provider
class LoggingFilter: ContainerRequestFilter, ContainerResponseFilter {
    private val log = Logger.getLogger(LoggingFilter::class.java)

    @Context
    lateinit var info: UriInfo

    @Context
    lateinit var request: HttpServerRequest

    override fun filter(context: ContainerRequestContext) {
        log.info("<<< ${context.method} ${info.path}")
    }

    override fun filter(r: ContainerRequestContext, context: ContainerResponseContext) {
        log.info(">>> ${r.method} ${info.path} ${context.status}")
    }
}
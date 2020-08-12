package com.beust.cedlinks

import org.jboss.logging.Logger
import javax.ws.rs.core.Response

open class BaseController {
    private val log = Logger.getLogger(BaseController::class.java)

    protected fun timeRequest(time: String, block: () -> Response.ResponseBuilder): Response {
        val result = try {
            if (time != Config.time) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                block()
            }
        } catch (ex: Exception) {
            log.error("Error: " + ex.message, ex)
            Response.serverError().entity(ex.message)
        }
        return result.build()
    }
}
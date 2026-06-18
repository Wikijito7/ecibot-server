package es.wokis.utils

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.paginated(block: suspend RoutingContext.(Int) -> Unit) {
    get("/page/{page}") {
        val page = call.parameters["page"]?.toIntOrNull()?.coerceAtLeast(1)
        page?.let {
            block(page)
        } ?: call.respond(HttpStatusCode.BadRequest, "Page as a number is required")
    }
}

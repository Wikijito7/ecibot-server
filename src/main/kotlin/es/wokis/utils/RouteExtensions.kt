package es.wokis.utils

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

suspend fun RoutingContext.requireString(fieldName: String, block: suspend RoutingContext.(String) -> Unit) {
    call.parameters[fieldName]?.takeIf { it.isNotEmpty() }?.let {
        block(it)
    } ?: call.respond(HttpStatusCode.BadRequest, "$fieldName is required")
}

suspend fun RoutingContext.requireInt(fieldName: String, block: suspend RoutingContext.(Int) -> Unit) {
    call.parameters[fieldName]?.takeIf { it.isNotEmpty() }?.toIntOrNull()?.let {
        block(it)
    } ?: call.respond(HttpStatusCode.BadRequest, "$fieldName is required")
}

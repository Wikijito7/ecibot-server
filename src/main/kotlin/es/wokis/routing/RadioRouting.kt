package es.wokis.routing

import es.wokis.data.mapper.radio.toDTO
import es.wokis.data.repository.radio.RadioRepository
import es.wokis.services.ImageService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpRadioRouting() {
    val radioRepository by inject<RadioRepository>()

    route("/radio") {
        get {
            val radios = radioRepository.getAllRadios()
            call.respond(HttpStatusCode.OK, radios)
        }

        get("/page/{page}") {
            val page = call.parameters["page"]?.toIntOrNull()?.coerceAtLeast(1)

            page?.let {
                val radioPage = radioRepository.getRadioPaginated(page).takeIf { it.radios.isNotEmpty() }?.toDTO()
                radioPage?.let {
                    call.respond(HttpStatusCode.OK, radioPage)
                } ?: call.respond(HttpStatusCode.NotFound, "Page not found")
            } ?: call.respond(HttpStatusCode.BadRequest, "Page as a number is required")
        }

        route("/{name}") {
            get {
                val name = call.parameters["name"]

                name?.let {
                    val radio = radioRepository.getRadioByName(name)?.toDTO()
                    radio?.let {
                        call.respond(HttpStatusCode.OK, radio)
                    } ?: call.respond(HttpStatusCode.NotFound, "No radio found")
                } ?: call.respond(HttpStatusCode.BadRequest, "Name is required")
            }

            get("/page/{page}") {

            }
        }

        route("/find/{name}") {
            get {
                val name = call.parameters["name"]

                name?.let {
                    val radios = radioRepository.findRadiosByPrompt(name).toDTO()
                    radios.takeIf { it.isNotEmpty() }?.let {
                        call.respond(HttpStatusCode.OK, radios)
                    } ?: call.respond(HttpStatusCode.NotFound, "No radio found")
                } ?: call.respond(HttpStatusCode.BadRequest, "Name is required")
            }

            get("/page/{page}") {

            }
        }
    }
}

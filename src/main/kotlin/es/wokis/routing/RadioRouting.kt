package es.wokis.routing

import es.wokis.data.repository.radio.RadioRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.setUpRadioRouting() {
    val radioRepository: RadioRepository by inject()

    get("/radio") {
        val radios = radioRepository.getAllRadios()
        call.respond(HttpStatusCode.OK, radios)
    }
}

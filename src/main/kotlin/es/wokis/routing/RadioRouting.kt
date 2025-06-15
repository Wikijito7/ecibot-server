package es.wokis.routing

import es.wokis.data.mapper.radio.toDTO
import es.wokis.data.repository.radio.RadioRepository
import es.wokis.utils.paginated
import es.wokis.utils.requireString
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

        paginated { page ->
            val radioPage = radioRepository.getRadiosPaginated(page).takeIf { it.radios.isNotEmpty() }?.toDTO()
            radioPage?.let {
                call.respond(HttpStatusCode.OK, radioPage)
            } ?: call.respond(HttpStatusCode.NotFound, "Page not found")
        }

        route("/{name}") {
            get {
                requireString("name") { name ->
                    val radio = radioRepository.getRadioByName(name)?.toDTO()
                    radio?.let {
                        call.respond(HttpStatusCode.OK, radio)
                    } ?: call.respond(HttpStatusCode.NotFound, "No radio found")
                }
            }
        }

        route("/find") {
            route("/name/{name}") {
                get {
                    requireString("name") { name ->
                        val radios = radioRepository.findRadiosByPrompt(name).toDTO()
                        radios.takeIf { it.isNotEmpty() }?.let {
                            call.respond(HttpStatusCode.OK, radios)
                        } ?: call.respond(HttpStatusCode.NotFound, "No radio found")
                    }
                }

                paginated { page ->
                    requireString("name") { name ->
                        val radioPage = radioRepository.findRadiosByPromptPaginated(name, page).takeIf { it.radios.isNotEmpty() }?.toDTO()
                        radioPage?.let {
                            call.respond(HttpStatusCode.OK, radioPage)
                        } ?: call.respond(HttpStatusCode.NotFound, "Page not found")
                    }
                }
            }

            route("/countrycode/{code}") {
                get {
                    requireString("code") { countryCode ->
                        val radios = radioRepository.getRadiosByCountry(countryCode).takeIf { it.isNotEmpty() }?.toDTO()
                        radios?.let {
                            call.respond(HttpStatusCode.OK, radios)
                        } ?: call.respond(HttpStatusCode.NotFound, "No radio found")
                    }
                }

                paginated { page ->
                    requireString("code") { countryCode ->
                        val radioPage = radioRepository.getRadiosByCountryPaginated(countryCode, page).takeIf { it.radios.isNotEmpty() }?.toDTO()
                        radioPage?.let {
                            call.respond(HttpStatusCode.OK, radioPage)
                        } ?: call.respond(HttpStatusCode.NotFound, "Page not found")
                    }
                }
            }
        }

        route("/countrycodes") {
            get {
                val countryCodes = radioRepository.getCountryCodes().takeIf { it.countryCodes.isNotEmpty() }?.toDTO()
                countryCodes?.let {
                    call.respond(HttpStatusCode.OK, countryCodes)
                } ?: call.respond(HttpStatusCode.NotFound, "No country codes found")
            }
        }
    }
}

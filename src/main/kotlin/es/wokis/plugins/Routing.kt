package es.wokis.plugins

import es.wokis.routing.*
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        setUpAuthRouting()
        setUpUserRouting()
        setUpSoundRouting()
        setUpStatsRouting()
        setUpRadioRouting()
    }
}

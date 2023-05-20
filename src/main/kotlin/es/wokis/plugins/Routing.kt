package es.wokis.plugins

import es.wokis.routing.setUpAuthRouting
import es.wokis.routing.setUpSoundRouting
import es.wokis.routing.setUpStatsRouting
import es.wokis.routing.setUpUserRouting
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        setUpAuthRouting()
        setUpUserRouting()
        setUpSoundRouting()
        setUpStatsRouting()
    }
}
